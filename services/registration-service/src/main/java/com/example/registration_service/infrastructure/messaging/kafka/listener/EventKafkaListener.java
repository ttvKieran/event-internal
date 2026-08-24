package com.example.registration_service.infrastructure.messaging.kafka.listener;

import com.example.registration_service.application.model.command.EventLifecycleCommand;
import com.example.registration_service.application.port.in.HandleEventLifecycle;
import com.example.registration_service.infrastructure.messaging.kafka.dto.inbound.EventCancelledMessage;
import com.example.registration_service.infrastructure.messaging.kafka.dto.inbound.EventDetailsConfiguredMessage;
import com.example.registration_service.infrastructure.messaging.kafka.dto.inbound.EventPublishedMessage;
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
public class EventKafkaListener {

    private final ObjectMapper objectMapper;
    private final HandleEventLifecycle handleEventLifecycle;

    @KafkaListener(topics = "Event", groupId = "${spring.kafka.consumer.group-id}")
    public void listenDebeziumOutbox(ConsumerRecord<String, String> record) {
        String messageId = "";
        try {
            Header typeHeader = record.headers().lastHeader("eventType");
            if (typeHeader == null) {
                log.warn("Không tìm thấy Header 'eventType'. Bỏ qua tin nhắn.");
                return;
            }
            String eventType = new String(typeHeader.value());

            // processed message
            Header msgIdHeader = record.headers().lastHeader("messageId");
            if (msgIdHeader == null) {
                log.warn("Không tìm thấy Header 'messageId'. Bỏ qua tin nhắn để đảm bảo an toàn.");
                return;
            }
            messageId = new String(msgIdHeader.value());

            String rawValue = record.value();
            JsonNode rootNode = objectMapper.readTree(rawValue);

            String actualPayloadStr = rootNode.has("payload")
                ? rootNode.get("payload").asText()
                : rawValue;

            log.info("Nhận sự kiện Kafka - Topic: 'Event', Loại: {}", eventType);

            // Phân luồng xử lý theo Loại sự kiện
            switch (eventType) {
                case "EventDetailsConfiguredEvent" -> {
                    EventDetailsConfiguredMessage message =
                        objectMapper.readValue(
                            actualPayloadStr,
                            EventDetailsConfiguredMessage.class
                        );

                    handleEventLifecycle.handle(
                        new EventLifecycleCommand.ConfigureCampaign(
                            messageId,
                            message.getEventId(),
                            message.getTicketType(),
                            message.getMaxParticipants(),
                            message.getPrice(),
                            message.getRegistrationOpenAt(),
                            message.getRegistrationCloseAt()
                        )
                    );
                }

                case "EventPublishedEvent" -> {
                    EventPublishedMessage message =
                        objectMapper.readValue(
                            actualPayloadStr,
                            EventPublishedMessage.class
                        );

                    handleEventLifecycle.handle(
                        new EventLifecycleCommand.ActivateCampaign(
                            messageId,
                            message.getEventId()
                        )
                    );
                }

                case "EventCancelledEvent" -> {
                    EventCancelledMessage message =
                        objectMapper.readValue(
                            actualPayloadStr,
                            EventCancelledMessage.class
                        );

                    handleEventLifecycle.handle(
                        new EventLifecycleCommand.CancelCampaign(
                            messageId,
                            message.getEventId()
                        )
                    );
                }

                default ->
                    log.debug(
                        "Bỏ qua event không hỗ trợ: {}",
                        eventType
                    );
            }

        } catch (Exception e) {
            log.error("Lỗi xử lý Kafka message {}", messageId, e);
            throw new KafkaMessageProcessingException(messageId, e);
        }
    }
}
