package com.example.notification_service.infrastructure.messaging.kafka.listener;

import com.example.notification_service.application.dto.message.EventCancelledMessage;
import com.example.notification_service.application.port.in.NotificationUseCase;
import com.example.notification_service.infrastructure.persistence.entity.ProcessedMessageEntity;
import com.example.notification_service.infrastructure.persistence.repository.JpaProcessedMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventKafkaListener {

    private final NotificationUseCase notificationUseCase;
    private final ObjectMapper objectMapper;
    private final JpaProcessedMessageRepository processedMessageRepository;

    @Transactional
    @RetryableTopic(
        attempts = "3",
        backOff = @BackOff(delay = 5000, multiplier = 2.0),
        dltTopicSuffix = ".DLT"
    )
    @KafkaListener(topics = "Event", groupId = "${spring.kafka.consumer.group-id}")
    public void onEventEvent(ConsumerRecord<String, String> record) {
        try {
            Header typeHeader = record.headers().lastHeader("eventType");
            if (typeHeader == null) { log.warn("[Notification] Thiếu header eventType. Bỏ qua."); return; }
            String eventType = new String(typeHeader.value());

            Header msgIdHeader = record.headers().lastHeader("messageId");
            if (msgIdHeader == null) { log.warn("[Notification] Thiếu header messageId. Bỏ qua."); return; }
            String messageId = new String(msgIdHeader.value());
            if (processedMessageRepository.existsById(messageId)) {
                log.info("[Notification] Tin nhắn {} đã xử lý. Bỏ qua!", messageId);
                return;
            }
            processedMessageRepository.save(new ProcessedMessageEntity(messageId, Instant.now()));

            // Unwrap payload
            String rawValue = record.value();
            JsonNode rootNode = objectMapper.readTree(rawValue);

            while (rootNode.isTextual()) {
                rawValue = rootNode.asText();
                rootNode = objectMapper.readTree(rawValue);
            }

            String actualPayload = rootNode.has("payload")
                ? rootNode.get("payload").asText()
                : rawValue;

            log.info("[Notification] Nhận event: {} | messageId: {}", eventType, messageId);

            switch (eventType) {
                case "EventCancelledEvent" -> {
                    EventCancelledMessage msg =
                        objectMapper.readValue(actualPayload, EventCancelledMessage.class);
                    notificationUseCase.handleEventCancelled(msg);
                }
                default -> log.debug("[Notification] Bỏ qua event không hỗ trợ: {}", eventType);
            }

        } catch (Exception e) {
            log.error("[Notification] Lỗi xử lý Kafka message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
