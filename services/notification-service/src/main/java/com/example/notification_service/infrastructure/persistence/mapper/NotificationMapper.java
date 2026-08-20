package com.example.notification_service.infrastructure.persistence.mapper;

import com.example.notification_service.domain.model.aggregate.Notification;
import com.example.notification_service.domain.model.valueobject.*;
import com.example.notification_service.infrastructure.persistence.entity.NotificationEntity;

public class NotificationMapper {

    public static NotificationEntity toEntity(Notification n) {
        NotificationEntity e = new NotificationEntity();
        e.setNotificationId(n.getNotificationId());
        e.setCorrelationId(n.getCorrelationId());
        e.setRecipientEmail(n.getRecipient().getEmail());
        e.setRecipientName(n.getRecipient().getFullName());
        e.setType(n.getType().getCode());
        e.setChannel(n.getChannel().getCode());
        e.setStatus(n.getStatus().getCode());
        e.setSubject(n.getSubject());
        e.setBody(n.getBody());
        e.setRetryCount(n.getRetryCount());
        e.setCreatedAt(n.getCreatedAt());
        e.setSentAt(n.getSentAt());
        return e;
    }

    public static Notification toDomain(NotificationEntity e) {
        return Notification.reconstitute(
            e.getNotificationId(),
            e.getCorrelationId(),
            RecipientInfo.of(e.getRecipientEmail(), e.getRecipientName()),
            NotificationType.of(e.getType()),
            NotificationChannel.of(e.getChannel()),
            NotificationStatus.of(e.getStatus()),
            e.getSubject(),
            e.getBody(),
            e.getRetryCount(),
            e.getCreatedAt(),
            e.getSentAt()
        );
    }
}
