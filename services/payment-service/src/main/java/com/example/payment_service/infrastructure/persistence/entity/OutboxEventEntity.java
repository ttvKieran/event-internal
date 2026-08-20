package com.example.payment_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
public class OutboxEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String aggregateType;   // "PaymentTransaction"

    @Column(nullable = false)
    private String aggregateId;     // paymentId.toString()

    @Column(nullable = false)
    private String type;       // "PaymentSucceededEvent" / "PaymentFailedEvent"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;         // JSON

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "correlation_id")
    private String correlationId;

    public static OutboxEventEntity of(String aggregateType, String aggregateId,
                                       String eventType, String payload) {
        OutboxEventEntity e = new OutboxEventEntity();
        e.aggregateType = aggregateType;
        e.aggregateId   = aggregateId;
        e.type     = eventType;
        e.payload       = payload;
        e.correlationId = java.util.UUID.randomUUID().toString(); // Tạo tạm
        return e;
    }
}
