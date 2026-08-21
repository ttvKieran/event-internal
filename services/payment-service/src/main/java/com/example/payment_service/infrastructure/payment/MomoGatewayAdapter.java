package com.example.payment_service.infrastructure.payment;

import com.example.payment_service.application.port.out.PaymentGatewayPort;
import com.example.payment_service.domain.model.valueobject.Money;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

@Slf4j
// @Component
public class MomoGatewayAdapter implements PaymentGatewayPort {

    @Override
    public String createPaymentUrl(UUID registrationId, Money amount, String orderInfo) {
        throw new UnsupportedOperationException(
            "Cổng thanh toán Momo chưa được hỗ trợ. Vui lòng chọn VNPAY.");
    }

    @Override
    public boolean verifyIpnSignature(Map<String, String> params) {
        throw new UnsupportedOperationException(
            "Cổng thanh toán Momo chưa được hỗ trợ.");
    }
}
