package com.example.registration_service.infrastructure.messaging.kafka.listener;

import com.example.registration_service.application.port.in.RegistrationUseCase;
import com.example.registration_service.infrastructure.persistence.entity.ProcessedMessageEntity;
import com.example.registration_service.infrastructure.persistence.repository.JpaProcessedMessageRepository;
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
import java.util.UUID;

/**
 * Command Handler — Nhận lệnh điều phối từ SagaManager (topic: registration-commands).
 * Thực thi lệnh bằng cách gọi RegistrationUseCase và đảm bảo bắn Event kết quả ra ngoài.
 *
 * Lệnh nhận:
 *   ConfirmPaidRegistrationCommand  → confirmRegistration() → RegistrationConfirmedEvent
 *   RollbackPaidRegistrationCommand → cancelRegistration()  → PaidRegistrationRolledBackEvent
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCommandListener {

    private final RegistrationUseCase registrationUseCase;
    private final ObjectMapper objectMapper;
    private final JpaProcessedMessageRepository processedMessageRepository;

    @Transactional
    @KafkaListener(topics = "Registration", groupId = "${spring.kafka.consumer.group-id}")
    public void onCommand(ConsumerRecord<String, String> record) {
        try {
            Header typeHeader = record.headers().lastHeader("eventType");
            if (typeHeader == null) { log.warn("[CommandHandler] Thiếu header eventType. Bỏ qua."); return; }
            String commandType = new String(typeHeader.value());

            // Idempotency
            Header msgIdHeader = record.headers().lastHeader("messageId");
            if (msgIdHeader == null) { log.warn("[CommandHandler] Thiếu header messageId. Bỏ qua."); return; }
            String messageId = new String(msgIdHeader.value());
            if (processedMessageRepository.existsById(messageId)) {
                log.info("[CommandHandler] Lệnh {} đã xử lý. Bỏ qua!", messageId);
                return;
            }
            processedMessageRepository.save(new ProcessedMessageEntity(messageId, Instant.now()));

            // Unwrap payload
            JsonNode rootNode = objectMapper.readTree(record.value());
            String actualPayload = rootNode.has("payload") ? rootNode.get("payload").asText() : record.value();
            JsonNode payloadNode = objectMapper.readTree(actualPayload);

            log.info("[CommandHandler] Nhận lệnh: {} | messageId: {}", commandType, messageId);

            switch (commandType) {
                case "ConfirmPaidRegistrationCommand" -> {
                    UUID registrationId = UUID.fromString(payloadNode.get("registrationId").asText());
                    registrationUseCase.confirmRegistration(registrationId);
                    log.info("[CommandHandler] Chốt vé thành công. registrationId={}", registrationId);
                }
                case "RollbackPaidRegistrationCommand" -> {
                    UUID registrationId = UUID.fromString(payloadNode.get("registrationId").asText());
                    String reason = payloadNode.get("reason").asText();
                    registrationUseCase.cancelRegistration(registrationId, reason);
                    log.info("[CommandHandler] Rollback vé thành công. registrationId={}, reason={}",
                        registrationId, reason);
                }
                default -> log.debug("[CommandHandler] Bỏ qua lệnh không hỗ trợ: {}", commandType);
            }

        } catch (Exception e) {
            log.error("[CommandHandler] Lỗi xử lý Command: {}", e.getMessage(), e);
        }
    }
}
