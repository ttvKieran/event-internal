package com.example.payment_service.application.port.out;

import com.example.payment_service.domain.model.valueobject.Money;

import java.util.Map;
import java.util.UUID;

public interface PaymentGatewayPort {

    // Tạo URL thanh toán để redirect/hiển thị QR cho người dùng.
    String createPaymentUrl(UUID registrationId, Money amount, String orderInfo);

    // Xác thực chữ ký từ IPN callback của cổng thanh toán. Bảo vệ hệ thống khỏi các request giả mạo.
    boolean verifyIpnSignature(Map<String, String> params);
}
