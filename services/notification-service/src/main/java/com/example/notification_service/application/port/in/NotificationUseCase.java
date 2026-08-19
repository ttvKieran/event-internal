package com.example.notification_service.application.port.in;

import com.example.notification_service.application.dto.message.EventCancelledMessage;
import com.example.notification_service.application.dto.message.PaidRegistrationRolledBackMessage;
import com.example.notification_service.application.dto.message.RegistrationConfirmedMessage;

public interface NotificationUseCase {

    // Vé PAID / FREE được chốt thành công → Gửi xác nhận cho nhân viên.
    void handleRegistrationConfirmed(RegistrationConfirmedMessage message);

    // Vé bị hủy (thanh toán thất bại / timeout / sự kiện bị hủy) → Gửi thông báo hủy vé.
    void handlePaidRegistrationRolledBack(PaidRegistrationRolledBackMessage message);

    // Sự kiện bị hủy bởi BTC → Gửi thông báo cho toàn bộ người đăng ký.
    void handleEventCancelled(EventCancelledMessage message);
}
