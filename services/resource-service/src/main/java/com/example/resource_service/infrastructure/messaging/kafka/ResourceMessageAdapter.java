package com.example.resource_service.infrastructure.messaging.kafka;

import com.example.resource_service.application.dto.message.ResourceConfiguredEventPayload;
import com.example.resource_service.application.dto.message.ResourceCreatedEventPayload;
import com.example.resource_service.application.port.out.ResourceMessagePort;
import com.example.resource_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.example.resource_service.infrastructure.persistence.repository.OutboxEventSpringRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResourceMessageAdapter implements ResourceMessagePort {
    private final OutboxEventSpringRepository outboxRepo;
    private final ObjectMapper objectMapper;

    private void saveOutboxEvent(String aggregateType, String aggregateId, String type, Object payload) {
        try {
            OutboxEventEntity entity = new OutboxEventEntity();
            entity.setId(UUID.randomUUID());
            entity.setCorrelationId(UUID.randomUUID());
            entity.setAggregateType(aggregateType);
            entity.setAggregateId(aggregateId);
            entity.setType(type);
            entity.setPayload(objectMapper.writeValueAsString(payload));
            entity.setCreatedAt(Instant.now());

            outboxRepo.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi serialize payload event", e);
        }
    }

    @Override
    public void publishResourceCreated(ResourceCreatedEventPayload payload) {
        saveOutboxEvent("Resource", payload.getResourceId(), "ResourceCreatedEvent", payload);
    }

    @Override
    public void publishResourceConfigured(ResourceConfiguredEventPayload payload) {
        saveOutboxEvent("Resource", payload.getResourceId(), "ResourceConfiguredEvent", payload);
    }
}
