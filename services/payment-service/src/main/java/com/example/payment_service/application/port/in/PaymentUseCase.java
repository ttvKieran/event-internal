package com.example.payment_service.application.port.in;

import com.example.payment_service.application.dto.command.VnPayIpnCommand;
import com.example.payment_service.application.dto.message.RegistrationRequestedPayload;

public interface PaymentUseCase {

    /**
     * Nhận event RegistrationRequestedEvent.
     * Tạo giao dịch PENDING + Sinh paymentUrl từ VNPay SDK.
     */
    void handleRegistrationRequested(RegistrationRequestedPayload payload);

    /**
     * Xử lý IPN Webhook từ VNPay gọi về.
     * Cập nhật trạng thái giao dịch + Bắn Event.
     */
    void handleVnPayIpn(VnPayIpnCommand command);

    /**
     * Scheduler gọi định kỳ mỗi 5 phút.
     * Quét giao dịch PENDING quá 15 phút → markAsExpired() → Bắn PaymentFailedEvent.
     */
    void expireStalePayments();
}
