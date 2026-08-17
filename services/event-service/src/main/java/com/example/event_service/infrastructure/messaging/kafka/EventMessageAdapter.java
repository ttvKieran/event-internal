package com.example.event_service.infrastructure.messaging.kafka;

import com.example.event_service.application.dto.message.EventCancelledEventPayload;
import com.example.event_service.application.dto.message.EventCreatedEventPayload;
import com.example.event_service.application.dto.message.EventDetailsConfiguredEventPayload;
import com.example.event_service.application.dto.message.EventPublishedEventPayload;
import com.example.event_service.application.port.out.EventMessagePort;
import com.example.event_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.example.event_service.infrastructure.persistence.repository.JpaOutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventMessageAdapter implements EventMessagePort {

    private final JpaOutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    private void saveOutboxEvent(UUID aggregateId, String type, String correlationId, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            OutboxEventEntity outboxEvent = new OutboxEventEntity(
                UUID.randomUUID(), "Event", aggregateId.toString(),
                type, correlationId, jsonPayload, Instant.now()
            );
            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi Parse JSON Outbox cho sự kiện " + type, e);
        }
    }

    @Override
    public void sendEventCreatedEvent(EventCreatedEventPayload payload, String correlationId) {
        saveOutboxEvent(payload.getEventId(), "EventCreatedEvent", correlationId, payload);
    }

    @Override
    public void sendEventDetailsConfiguredEvent(EventDetailsConfiguredEventPayload payload, String correlationId) {
        saveOutboxEvent(payload.getEventId(), "EventDetailsConfiguredEvent", correlationId, payload);
    }

    @Override
    public void sendEventPublishedEvent(EventPublishedEventPayload payload, String correlationId) {
        saveOutboxEvent(payload.getEventId(), "EventPublishedEvent", correlationId, payload);
    }

    @Override
    public void sendEventCancelledEvent(EventCancelledEventPayload payload, String correlationId) {
        saveOutboxEvent(payload.getEventId(), "EventCancelledEvent", correlationId, payload);
    }
}
