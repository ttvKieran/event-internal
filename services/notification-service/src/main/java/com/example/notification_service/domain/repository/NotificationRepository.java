package com.example.notification_service.domain.repository;

import com.example.notification_service.domain.model.aggregate.Notification;
import com.example.notification_service.domain.model.valueobject.NotificationStatus;

import java.util.List;

public interface NotificationRepository {
    Notification save(Notification notification);
    List<Notification> findByStatus(NotificationStatus status);
}
