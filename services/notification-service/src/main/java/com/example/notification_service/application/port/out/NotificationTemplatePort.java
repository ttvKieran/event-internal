package com.example.notification_service.application.port.out;

import com.example.notification_service.application.dto.message.EventCancelledMessage;
import com.example.notification_service.application.dto.message.PaidRegistrationRolledBackMessage;
import com.example.notification_service.application.dto.message.RegistrationConfirmedMessage;

public interface NotificationTemplatePort {

    // Render HTML email xác nhận vé, trả về chuỗi HTML.
    String renderTicketConfirmed(RegistrationConfirmedMessage message);

    // Render HTML email hủy vé.
    String renderTicketCancelled(PaidRegistrationRolledBackMessage message);

    // Render HTML email thông báo sự kiện bị hủy.
    String renderEventCancelled(EventCancelledMessage message);
}
