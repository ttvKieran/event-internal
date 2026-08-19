package com.example.registration_service.infrastructure.messaging.kafka.listener;

import com.example.registration_service.application.dto.message.PaymentFailedMessage;
import com.example.registration_service.application.dto.message.PaymentSucceededMessage;
import com.example.registration_service.application.port.out.SagaCommandPort;
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

/**
 * Saga Orchestrator — Lắng nghe kết quả từ Payment Service.
 * Sau khi nhận được kết quả, phát Command tương ứng vào topic "registration-commands".
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaListener {

    private final SagaCommandPort sagaCommandPort;
    private final ObjectMapper objectMapper;
    private final JpaProcessedMessageRepository processedMessageRepository;

    @Transactional
    @KafkaListener(topics = "Payment", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentEvent(ConsumerRecord<String, String> record) {
        try {
            Header typeHeader = record.headers().lastHeader("eventType");
            if (typeHeader == null) { log.warn("[SagaManager] Thiếu header eventType. Bỏ qua."); return; }
            String eventType = new String(typeHeader.value());

            // Idempotency
            Header msgIdHeader = record.headers().lastHeader("messageId");
            if (msgIdHeader == null) { log.warn("[SagaManager] Thiếu header messageId. Bỏ qua."); return; }
            String messageId = new String(msgIdHeader.value());
            if (processedMessageRepository.existsById(messageId)) {
                log.info("[SagaManager] Tin nhắn {} đã xử lý. Bỏ qua!", messageId);
                return;
            }
            processedMessageRepository.save(new ProcessedMessageEntity(messageId, Instant.now()));

            // Unwrap payload
            JsonNode rootNode = objectMapper.readTree(record.value());
            String actualPayload = rootNode.has("payload") ? rootNode.get("payload").asText() : record.value();

            log.info("[SagaManager] Nhận event: {} | messageId: {}", eventType, messageId);

            // Phân luồng điều phối Saga
            switch (eventType) {
                case "PaymentSucceededEvent" -> {
                    PaymentSucceededMessage msg =
                        objectMapper.readValue(actualPayload, PaymentSucceededMessage.class);
                    // Thanh toán OK → Ra lệnh chốt vé
                    sagaCommandPort.sendConfirmCommand(msg.getRegistrationId(), msg.getPaymentId());
                }
                case "PaymentFailedEvent" -> {
                    PaymentFailedMessage msg =
                        objectMapper.readValue(actualPayload, PaymentFailedMessage.class);
                    // Thanh toán thất bại/hết hạn → Ra lệnh hoàn trả slot
                    sagaCommandPort.sendRollbackCommand(msg.getRegistrationId(), msg.getReason());
                }
                default -> log.debug("[SagaManager] Bỏ qua event không hỗ trợ: {}", eventType);
            }

        } catch (Exception e) {
            log.error("[SagaManager] Lỗi xử lý Kafka message: {}", e.getMessage(), e);
        }
    }
}
