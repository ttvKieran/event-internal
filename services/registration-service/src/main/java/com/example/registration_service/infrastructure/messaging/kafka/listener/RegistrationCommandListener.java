package com.example.registration_service.infrastructure.messaging.kafka.listener;

import com.example.registration_service.application.model.command.RegistrationCommand;
import com.example.registration_service.application.port.in.HandleRegistrationCommand;
import com.example.registration_service.infrastructure.messaging.kafka.exception.KafkaMessageProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCommandListener {

    private final ObjectMapper objectMapper;
    private final HandleRegistrationCommand handleRegistrationCommand;

    @KafkaListener(topics = "Registration", groupId = "${spring.kafka.consumer.group-id}")
    public void onCommand(ConsumerRecord<String, String> record) {
        String messageId = "";
        try {
            Header typeHeader = record.headers().lastHeader("eventType");
            if (typeHeader == null) { log.warn("[CommandHandler] Thiếu header eventType. Bỏ qua."); return; }
            String commandType = new String(typeHeader.value());

            // Idempotency
            Header msgIdHeader = record.headers().lastHeader("messageId");
            if (msgIdHeader == null) { log.warn("[CommandHandler] Thiếu header messageId. Bỏ qua."); return; }
            messageId = new String(msgIdHeader.value());

            // Unwrap payload
            JsonNode rootNode = objectMapper.readTree(record.value());
            String actualPayload = rootNode.has("payload") ? rootNode.get("payload").asText() : record.value();
            JsonNode payloadNode = objectMapper.readTree(actualPayload);

            log.info("[CommandHandler] Nhận lệnh: {} | messageId: {}", commandType, messageId);

            switch (commandType) {
                case "ConfirmPaidRegistrationCommand" -> {
                    UUID registrationId = UUID.fromString(
                        payloadNode.get("registrationId").asText()
                    );

                    handleRegistrationCommand.handle(
                        new RegistrationCommand.ConfirmPaid(
                            messageId,
                            registrationId
                        )
                    );
                }

                case "RollbackPaidRegistrationCommand" -> {
                    UUID registrationId = UUID.fromString(
                        payloadNode.get("registrationId").asText()
                    );
                    String reason = payloadNode.get("reason").asText();

                    handleRegistrationCommand.handle(
                        new RegistrationCommand.RollbackPaid(
                            messageId,
                            registrationId,
                            reason
                        )
                    );
                }

                case "AsyncReserveTicketCommand" -> {
                    UUID campaignId = UUID.fromString(
                        payloadNode.get("campaignId").asText()
                    );
                    UUID userId = UUID.fromString(
                        payloadNode.get("userId").asText()
                    );

                    String providerCode = null;
                    JsonNode providerNode = payloadNode.get("provider");

                    if (providerNode != null && !providerNode.isNull()) {
                        providerCode = providerNode
                            .path("code")
                            .asText(null);
                    }

                    handleRegistrationCommand.handle(
                        new RegistrationCommand.ReserveTicketAsync(
                            messageId,
                            campaignId,
                            userId,
                            providerCode
                        )
                    );
                }

                default ->
                    log.debug(
                        "Bỏ qua command không hỗ trợ: {}",
                        commandType
                    );
            }
        } catch (Exception e) {
            log.error("Lỗi xử lý Kafka message {}", messageId, e);
            throw new KafkaMessageProcessingException(messageId, e);
        }
    }
}
