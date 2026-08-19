package com.example.notification_service.application.service;

import com.example.notification_service.application.dto.message.EventCancelledMessage;
import com.example.notification_service.application.dto.message.PaidRegistrationRolledBackMessage;
import com.example.notification_service.application.dto.message.RegistrationConfirmedMessage;
import com.example.notification_service.application.port.in.NotificationUseCase;
import com.example.notification_service.application.port.out.NotificationSenderPort;
import com.example.notification_service.application.port.out.NotificationTemplatePort;
import com.example.notification_service.domain.model.aggregate.Notification;
import com.example.notification_service.domain.model.valueobject.NotificationChannel;
import com.example.notification_service.domain.model.valueobject.NotificationType;
import com.example.notification_service.domain.model.valueobject.RecipientInfo;
import com.example.notification_service.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationSenderPort senderPort;
    private final NotificationTemplatePort templatePort;

    @Override
    public void handleRegistrationConfirmed(RegistrationConfirmedMessage message) {
        log.info("[Notification] Xử lý RegistrationConfirmed cho registrationId={}",
            message.getRegistrationId());

        // TODO: Lookup email của userId từ IAM Service
        // Tạm thời dùng mock email để demo
        RecipientInfo recipient = RecipientInfo.of("tatruongvu1708@gmail.com", "Trường Vũ");

        String html = templatePort.renderTicketConfirmed(message);

        Notification notification = Notification.createNew(
            message.getRegistrationId(),
            recipient,
            NotificationType.TICKET_CONFIRMED,
            NotificationChannel.EMAIL,
            "[VTIT Events] Vé của bạn đã được xác nhận!",
            html
        );

        notificationRepository.save(notification);
        senderPort.send(notification);
        notification.markAsSent();
        notificationRepository.save(notification);

        log.info("[Notification] Đã gửi email xác nhận vé. notificationId={}",
            notification.getNotificationId());
    }

    @Override
    public void handlePaidRegistrationRolledBack(PaidRegistrationRolledBackMessage message) {
        log.info("[Notification] Xử lý RegistrationCancelled cho registrationId={}",
            message.getRegistrationId());

        RecipientInfo recipient = RecipientInfo.of("tatruongvu1708@gmail.com", "Trường Vũ");

        String html = templatePort.renderTicketCancelled(message);

        Notification notification = Notification.createNew(
            message.getRegistrationId(),
            recipient,
            NotificationType.TICKET_CANCELLED,
            NotificationChannel.EMAIL,
            "[VTIT Events] Thông báo hủy vé",
            html
        );

        notificationRepository.save(notification);
        senderPort.send(notification);
        notification.markAsSent();
        notificationRepository.save(notification);

        log.info("[Notification] Đã gửi email hủy vé. notificationId={}",
            notification.getNotificationId());
    }

    @Override
    public void handleEventCancelled(EventCancelledMessage message) {
        log.info("[Notification] Xử lý EventCancelled cho eventId={}", message.getEventId());

        RecipientInfo recipient = RecipientInfo.of("tatruongvu1708@gmail.com", "Trường Vũ");

        String html = templatePort.renderEventCancelled(message);

        Notification notification = Notification.createNew(
            message.getEventId(),
            recipient,
            NotificationType.EVENT_CANCELLED,
            NotificationChannel.EMAIL,
            "[VTIT Events] Sự kiện đã bị hủy",
            html
        );

        notificationRepository.save(notification);
        senderPort.send(notification);
        notification.markAsSent();
        notificationRepository.save(notification);

        log.info("[Notification] Đã gửi email thông báo hủy sự kiện. notificationId={}",
            notification.getNotificationId());
    }
}
