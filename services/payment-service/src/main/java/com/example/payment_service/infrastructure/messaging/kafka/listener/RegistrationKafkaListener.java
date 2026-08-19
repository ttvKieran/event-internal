package com.example.payment_service.infrastructure.messaging.kafka.listener;

import com.example.payment_service.application.dto.message.RegistrationRequestedPayload;
import com.example.payment_service.application.port.in.PaymentUseCase;
import com.example.payment_service.infrastructure.persistence.entity.ProcessedMessageEntity;
import com.example.payment_service.infrastructure.persistence.repository.JpaProcessedMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationKafkaListener {

    private final PaymentUseCase paymentUseCase;
    private final ObjectMapper objectMapper;
    private final JpaProcessedMessageRepository processedMessageRepository;

    @Transactional
    @KafkaListener(topics = "Registration", groupId = "${spring.kafka.consumer.group-id}")
    public void onRegistrationEvent(ConsumerRecord<String, String> record) {
        try {
            // ── Đọc Header eventType ─────────────────────────────────────────
            Header typeHeader = record.headers().lastHeader("eventType");
            if (typeHeader == null) {
                log.warn("[Payment] Không tìm thấy Header 'eventType'. Bỏ qua.");
                return;
            }
            String eventType = new String(typeHeader.value());

            // ── Idempotency: Kiểm tra tin nhắn đã xử lý chưa ────────────────
            Header msgIdHeader = record.headers().lastHeader("messageId");
            if (msgIdHeader == null) {
                log.warn("[Payment] Không tìm thấy Header 'messageId'. Bỏ qua để an toàn.");
                return;
            }
            String messageId = new String(msgIdHeader.value());
            if (processedMessageRepository.existsById(messageId)) {
                log.info("[Payment] Tin nhắn {} đã xử lý trước đó. Bỏ qua!", messageId);
                return;
            }
            processedMessageRepository.save(new ProcessedMessageEntity(messageId, Instant.now()));

            // ── Unwrap payload (giống EventKafkaListener) ────────────────────
            String rawValue = record.value();
            JsonNode rootNode = objectMapper.readTree(rawValue);
            String actualPayload = rootNode.has("payload")
                ? rootNode.get("payload").asText()
                : rawValue;

            log.info("[Payment] Nhận sự kiện: {} | messageId: {}", eventType, messageId);

            // ── Phân luồng xử lý ─────────────────────────────────────────────
            switch (eventType) {
                case "RegistrationRequestedEvent" -> {
                    RegistrationRequestedPayload payload =
                        objectMapper.readValue(actualPayload, RegistrationRequestedPayload.class);
                    paymentUseCase.handleRegistrationRequested(payload);
                    log.info("[Payment] Tạo giao dịch thành công cho registrationId={}",
                        payload.getRegistrationId());
                }
                default -> log.debug("[Payment] Bỏ qua sự kiện không hỗ trợ: {}", eventType);
            }

        } catch (Exception e) {
            log.error("[Payment] Lỗi xử lý Kafka message: {}", e.getMessage(), e);
        }
    }
}
