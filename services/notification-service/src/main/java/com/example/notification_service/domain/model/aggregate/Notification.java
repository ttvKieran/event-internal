package com.example.notification_service.domain.model.aggregate;

import com.example.notification_service.domain.model.valueobject.NotificationChannel;
import com.example.notification_service.domain.model.valueobject.NotificationStatus;
import com.example.notification_service.domain.model.valueobject.NotificationType;
import com.example.notification_service.domain.model.valueobject.RecipientInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "notificationId")
public class Notification {

    private UUID notificationId;
    private UUID correlationId;       // registrationId / eventId để trace
    private RecipientInfo recipient;
    private NotificationType type;
    private NotificationStatus status;
    private NotificationChannel channel;
    private String subject;
    private String body;              // HTML đã render sẵn
    private int retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    private Notification() {}

    public static Notification createNew(
        UUID correlationId,
        RecipientInfo recipient,
        NotificationType type,
        NotificationChannel channel,
        String subject,
        String body
    ) {
        Notification n = new Notification();
        n.notificationId = UUID.randomUUID();
        n.correlationId = correlationId;
        n.recipient = recipient;
        n.type = type;
        n.status = NotificationStatus.PENDING;
        n.subject = subject;
        n.channel = channel;
        n.body = body;
        n.retryCount = 0;
        n.createdAt = LocalDateTime.now();
        return n;
    }

    public static Notification reconstitute(
        UUID notificationId, UUID correlationId,
        RecipientInfo recipient, NotificationType type,
        NotificationChannel channel,
        NotificationStatus status, String subject, String body,
        int retryCount, LocalDateTime createdAt, LocalDateTime sentAt
    ) {
        Notification n = new Notification();
        n.notificationId = notificationId;
        n.correlationId = correlationId;
        n.recipient = recipient;
        n.type = type;
        n.status = status;
        n.subject = subject;
        n.body = body;
        n.channel = channel;
        n.retryCount = retryCount;
        n.createdAt = createdAt;
        n.sentAt = sentAt;
        return n;
    }

    public void markAsSent() {
        if (this.status == NotificationStatus.SENT) return; // Idempotent
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.retryCount++;
        this.status = NotificationStatus.FAILED;
    }
}
