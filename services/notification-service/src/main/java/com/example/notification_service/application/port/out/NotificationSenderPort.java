package com.example.notification_service.application.port.out;

import com.example.notification_service.domain.model.aggregate.Notification;

public interface NotificationSenderPort {

    void send(Notification notification);
}
