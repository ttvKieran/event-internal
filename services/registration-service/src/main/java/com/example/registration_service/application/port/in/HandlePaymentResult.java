package com.example.registration_service.application.port.in;

import com.example.registration_service.application.model.command.PaymentResultCommand;

@FunctionalInterface
public interface HandlePaymentResult {

    void handle(PaymentResultCommand command);
}
