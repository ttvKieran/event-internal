package com.example.payment_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * State Machine:
 *   PENDING → SUCCESS   (VNPay IPN báo thành công)
 *   PENDING → FAILED    (VNPay IPN báo thất bại)
 *   PENDING → EXPIRED   (Scheduler quét sau 15 phút)
 *   SUCCESS → REFUNDED  (Tương lai — hoàn tiền khi Event bị hủy)
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentStatus {

    public static final PaymentStatus PENDING  = new PaymentStatus("PENDING");
    public static final PaymentStatus SUCCESS  = new PaymentStatus("SUCCESS");
    public static final PaymentStatus FAILED   = new PaymentStatus("FAILED");
    public static final PaymentStatus EXPIRED  = new PaymentStatus("EXPIRED");
    public static final PaymentStatus REFUNDED = new PaymentStatus("REFUNDED");

    private final String code;

    public static PaymentStatus of(String code) {
        if (code == null) throw new IllegalArgumentException("Trạng thái thanh toán không được trống");
        return switch (code.toUpperCase()) {
            case "PENDING"  -> PENDING;
            case "SUCCESS"  -> SUCCESS;
            case "FAILED"   -> FAILED;
            case "EXPIRED"  -> EXPIRED;
            case "REFUNDED" -> REFUNDED;
            default -> throw new IllegalArgumentException("Trạng thái không hợp lệ: " + code);
        };
    }
}
