package com.example.registration_service.application.port.out;

import com.example.registration_service.domain.model.aggregate.Registration;

public interface RegistrationMessagePort {

    /**
     * Bắn khi vé PAID vừa được giữ chỗ thành công.
     → Payment Service lắng nghe để tạo giao dịch thanh toán và sinh link QR.
     */
    void publishRegistrationRequested(Registration registration, java.math.BigDecimal amount, String provider);

    /**
     * Bắn khi vé đã được chốt thành công (FREE tự chốt, PAID sau khi thanh toán xong).
     → Notification Service lắng nghe để gửi email xác nhận cho nhân viên.
     */
    void publishRegistrationConfirmed(Registration registration);

    /**
     * Bắn khi Saga rollback — Vé PAID bị thu hồi do thanh toán thất bại/timeout.
     → Notification Service lắng nghe để gửi email thông báo hủy vé.
     */
    void publishPaidRegistrationRolledBack(Registration registration, String reason);
}
