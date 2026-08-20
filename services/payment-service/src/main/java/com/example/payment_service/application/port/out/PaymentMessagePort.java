package com.example.payment_service.application.port.out;

import com.example.payment_service.domain.model.aggregate.PaymentTransaction;

public interface PaymentMessagePort {

    // Bắn PaymentSucceededEvent → Registration SagaManager lắng nghe để chốt vé
    void publishPaymentSucceeded(PaymentTransaction transaction);

    // Bắn PaymentFailedEvent → Registration SagaManager lắng nghe để rollback vé
    void publishPaymentFailed(PaymentTransaction transaction, String reason);
}
