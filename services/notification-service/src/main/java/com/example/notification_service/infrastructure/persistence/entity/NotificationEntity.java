package com.example.notification_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class NotificationEntity {

    @Id
    private UUID notificationId;

    private UUID correlationId;

    private String recipientEmail;
    private String recipientName;

    private String type;      // NotificationType.code
    private String channel;   // NotificationChannel.code
    private String status;    // NotificationStatus.code

    @Column(length = 500)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    private int retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
