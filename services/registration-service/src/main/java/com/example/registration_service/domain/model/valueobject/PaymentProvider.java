package com.example.registration_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentProvider {
    public static final PaymentProvider VNPAY = new PaymentProvider("VNPAY");
    public static final PaymentProvider MOMO = new PaymentProvider("MOMO");

    private final String code;

    public static PaymentProvider of(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return switch (code.toUpperCase()) {
            case "VNPAY" -> VNPAY;
            case "MOMO" -> MOMO;
            default -> throw new IllegalArgumentException("Cổng thanh toán không hợp lệ: " + code + ". Chỉ hỗ trợ VNPAY hoặc MOMO.");
        };
    }
}
