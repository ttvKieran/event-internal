package com.example.notification_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "processed_messages")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedMessageEntity {

    @Id
    @Column(length = 255)
    private String messageId;

    @Column(nullable = false)
    private Instant processedAt;
}
