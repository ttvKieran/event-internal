package com.example.registration_service.application.model.command;

import java.util.UUID;

public sealed interface PaymentResultCommand {

    String messageId();

    record Succeeded(
        String messageId,
        UUID registrationId,
        UUID paymentId
    ) implements PaymentResultCommand { }

    record Failed(
        String messageId,
        UUID registrationId,
        String reason
    ) implements PaymentResultCommand { }
}
