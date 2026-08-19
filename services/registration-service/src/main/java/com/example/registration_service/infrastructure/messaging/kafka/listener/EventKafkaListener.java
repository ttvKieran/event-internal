package com.example.registration_service.infrastructure.messaging.kafka.listener;

import com.example.registration_service.application.dto.message.EventCancelledMessage;
import com.example.registration_service.application.dto.message.EventDetailsConfiguredMessage;
import com.example.registration_service.application.dto.message.EventPublishedMessage;
import com.example.registration_service.application.port.in.RegistrationCampaignUseCase;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class EventKafkaListener {

    private final RegistrationCampaignUseCase campaignUseCase;
    private final ObjectMapper objectMapper;
    private final JpaProcessedMessageRepository processedMessageRepository;

    @Transactional
    @KafkaListener(topics = "Event", groupId = "${spring.kafka.consumer.group-id}")
    public void listenDebeziumOutbox(ConsumerRecord<String, String> record) {
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
            String messageId = new String(msgIdHeader.value());
            if (processedMessageRepository.existsById(messageId)) {
                log.info("Tin nhắn {} (Loại: {}) đã được xử lý. Bỏ qua!", messageId, eventType);
                return;
            }
            processedMessageRepository.save(new ProcessedMessageEntity(messageId, Instant.now()));

            String rawValue = record.value();
            JsonNode rootNode = objectMapper.readTree(rawValue);

            String actualPayloadStr = rootNode.has("payload")
                ? rootNode.get("payload").asText()
                : rawValue;

            log.info("Nhận sự kiện Kafka - Topic: 'Event', Loại: {}", eventType);

            // Phân luồng xử lý theo Loại sự kiện
            switch (eventType) {
                case "EventDetailsConfiguredEvent":
                    EventDetailsConfiguredMessage configuredMsg = objectMapper.readValue(actualPayloadStr, EventDetailsConfiguredMessage.class);
                    campaignUseCase.createCampaignSnapshot(
                        configuredMsg.getEventId(),
                        configuredMsg.getTicketType(),
                        configuredMsg.getMaxParticipants(),
                        configuredMsg.getPrice(),
                        configuredMsg.getRegistrationOpenAt(),
                        configuredMsg.getRegistrationCloseAt()
                    );
                    log.info("Xử lý thành công khởi tạo kho vé cho Sự kiện: {}", configuredMsg.getEventId());
                    break;

                case "EventPublishedEvent":
                    EventPublishedMessage publishedMsg = objectMapper.readValue(actualPayloadStr, EventPublishedMessage.class);
                    campaignUseCase.activateCampaign(publishedMsg.getEventId());
                    log.info("Xử lý thành công mở bán vé cho Sự kiện: {}", publishedMsg.getEventId());
                    break;

                case "EventCancelledEvent":
                    EventCancelledMessage cancelledMsg = objectMapper.readValue(actualPayloadStr, EventCancelledMessage.class);
                    campaignUseCase.cancelCampaign(cancelledMsg.getEventId());
                    log.info("Xử lý thành công hủy bỏ Sự kiện: {}", cancelledMsg.getEventId());
                    break;

                default:
                    log.debug("Bỏ qua sự kiện không được hỗ trợ: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Lỗi khi phân tích hoặc xử lý tin nhắn Kafka. Chi tiết: {}", e.getMessage(), e);
        }
    }
}
