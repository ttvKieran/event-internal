package com.example.iam_service.infrastructure.persistence.mapper;

import com.example.iam_service.domain.model.OutboxEvent;
import com.example.iam_service.infrastructure.persistence.entity.OutboxEventJpaEntity;

import java.util.UUID;

public class OutboxEventMapper {

    public static OutboxEventJpaEntity toJpaEntity(OutboxEvent domain) {
        if (domain == null) return null;
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
        if (domain.getId() != null) {
            entity.setId(UUID.fromString(domain.getId()));
        }
        entity.setAggregateType(domain.getAggregateType());
        entity.setAggregateId(domain.getAggregateId());
        entity.setType(domain.getType());
        entity.setPayload(domain.getPayload());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public static OutboxEvent toDomainModel(OutboxEventJpaEntity entity) {
        if (entity == null) return null;
        return OutboxEvent.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .aggregateType(entity.getAggregateType())
                .aggregateId(entity.getAggregateId())
                .type(entity.getType())
                .payload(entity.getPayload())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
