package com.example.registration_service.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "processed_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedMessageEntity {

    @Id
    @Column(name = "message_id", length = 255, nullable = false)
    private String messageId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}
