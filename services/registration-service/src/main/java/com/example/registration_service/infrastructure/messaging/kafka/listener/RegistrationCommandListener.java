package com.example.registration_service.infrastructure.messaging.kafka.listener;

import com.example.registration_service.application.model.command.RegistrationCommand;
import com.example.registration_service.application.port.in.HandleRegistrationCommand;
import com.example.registration_service.infrastructure.messaging.kafka.dto.inbound.AsyncReserveTicketCommandMessage;
import com.example.registration_service.infrastructure.messaging.kafka.dto.inbound.ConfirmPaidRegistrationMessage;
import com.example.registration_service.infrastructure.messaging.kafka.dto.inbound.RollbackPaidRegistrationMessage;
import com.example.registration_service.infrastructure.messaging.kafka.exception.KafkaMessageProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCommandListener {

    private final ObjectMapper objectMapper;
    private final HandleRegistrationCommand handleRegistrationCommand;

    @KafkaListener(topics = "Registration", groupId = "${spring.kafka.consumer.group-id}")
    public void onCommand(ConsumerRecord<String, String> record) {
        String messageId = "unknown";
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

            log.info("[CommandHandler] Nhận lệnh: {} | messageId: {}", commandType, messageId);

            switch (commandType) {
                case "ConfirmPaidRegistrationCommand" -> {
                    ConfirmPaidRegistrationMessage message =
                        objectMapper.readValue(actualPayload, ConfirmPaidRegistrationMessage.class);

                    handleRegistrationCommand.handle(new RegistrationCommand.ConfirmPaid(messageId, message.getRegistrationId()));
                }
                case "RollbackPaidRegistrationCommand" -> {
                    RollbackPaidRegistrationMessage message =
                        objectMapper.readValue(actualPayload, RollbackPaidRegistrationMessage.class);

                    handleRegistrationCommand.handle(
                        new RegistrationCommand.RollbackPaid(messageId, message.getRegistrationId(), message.getReason())
                    );
                }
                case "AsyncReserveTicketCommand" -> {
                    AsyncReserveTicketCommandMessage message =
                        objectMapper.readValue(actualPayload, AsyncReserveTicketCommandMessage.class);

                    String providerCode = message.getProvider() == null ? null : message.getProvider().getCode();

                    handleRegistrationCommand.handle(
                        new RegistrationCommand.ReserveTicketAsync(messageId, message.getCampaignId(), message.getUserId(), providerCode)
                    );
                }
                default -> log.debug("Bỏ qua command không hỗ trợ: {}", commandType);
            }
        } catch (Exception e) {
            log.error("Lỗi xử lý Kafka message {}", messageId, e);
            throw new KafkaMessageProcessingException(messageId, e);
        }
    }
}
