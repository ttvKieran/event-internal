package com.example.iam_service.infrastructure.messaging;

import com.example.iam_service.application.dto.message.EmployeeEventPayload;
import com.example.iam_service.application.port.out.IamMessagePort;
import com.example.iam_service.infrastructure.persistence.entity.OutboxEventJpaEntity;
import com.example.iam_service.infrastructure.persistence.repository.OutboxEventSpringDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IamMessageAdapter implements IamMessagePort {

    private final OutboxEventSpringDataRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public IamMessageAdapter(OutboxEventSpringDataRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishEmployeeEvent(String eventType, EmployeeEventPayload payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
            entity.setAggregateType("Employee");
            entity.setAggregateId(payload.getEmployeeId());
            entity.setType(eventType);
            entity.setPayload(payloadJson);
            entity.setCreatedAt(LocalDateTime.now());
            
            outboxRepository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize employee event payload", e);
        }
    }
}
