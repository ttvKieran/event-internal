package com.example.analytics_service.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEventEntity {
    @Id
    private UUID id;
    private String aggregateType;
    private String aggregateId;
    private String type;
    private String correlationId;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private Instant createdAt;
}
