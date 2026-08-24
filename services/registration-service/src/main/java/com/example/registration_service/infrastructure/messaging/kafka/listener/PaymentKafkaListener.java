package com.example.registration_service.infrastructure.messaging.kafka.listener;

import com.example.registration_service.application.model.command.PaymentResultCommand;
import com.example.registration_service.application.port.in.HandlePaymentResult;
import com.example.registration_service.infrastructure.messaging.kafka.dto.inbound.PaymentFailedMessage;
import com.example.registration_service.infrastructure.messaging.kafka.dto.inbound.PaymentSucceededMessage;
import com.example.registration_service.infrastructure.messaging.kafka.exception.KafkaMessageProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Saga Orchestrator — Lắng nghe kết quả từ Payment Service.
 * Sau khi nhận được kết quả, phát Command tương ứng vào topic "registration-commands".
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaListener {

    private final ObjectMapper objectMapper;
    private final HandlePaymentResult handlePaymentResult;

    @KafkaListener(
        topics = "Payment",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onPaymentEvent(ConsumerRecord<String, String> record) {
        String messageId = "unknown";

        try {
            Header typeHeader = record.headers().lastHeader("eventType");
            if (typeHeader == null) {
                log.warn("[SagaManager] Thiếu header eventType.");
                return;
            }

            Header messageIdHeader =
                record.headers().lastHeader("messageId");
            if (messageIdHeader == null) {
                log.warn("[SagaManager] Thiếu header messageId.");
                return;
            }

            String eventType = new String(typeHeader.value());
            messageId = new String(messageIdHeader.value());

            JsonNode root = objectMapper.readTree(record.value());
            String payload = root.has("payload")
                ? root.get("payload").asText()
                : record.value();

            switch (eventType) {
                case "PaymentSucceededEvent" -> {
                    PaymentSucceededMessage message =
                        objectMapper.readValue(
                            payload,
                            PaymentSucceededMessage.class
                        );

                    handlePaymentResult.handle(
                        new PaymentResultCommand.Succeeded(
                            messageId,
                            message.getRegistrationId(),
                            message.getPaymentId()
                        )
                    );
                }

                case "PaymentFailedEvent" -> {
                    PaymentFailedMessage message =
                        objectMapper.readValue(
                            payload,
                            PaymentFailedMessage.class
                        );

                    handlePaymentResult.handle(
                        new PaymentResultCommand.Failed(
                            messageId,
                            message.getRegistrationId(),
                            message.getReason()
                        )
                    );
                }

                default ->
                    log.debug("Bỏ qua event không hỗ trợ: {}", eventType);
            }
        } catch (Exception exception) {
            log.error(
                "Lỗi xử lý Kafka message {}",
                messageId,
                exception
            );
            throw new KafkaMessageProcessingException(
                messageId,
                exception
            );
        }
    }
}
