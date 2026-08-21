package com.example.resource_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Data
public class OutboxEventEntity {
    @Id
    private UUID id;

    private UUID correlationId;

    private String aggregateType;
    private String aggregateId;
    private String type;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Instant createdAt;
}
