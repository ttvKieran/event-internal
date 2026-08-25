package com.example.registration_service.application.model.command;

import java.util.UUID;

public sealed interface RegistrationCommand {

    String messageId();

    record ConfirmPaid(
        String messageId,
        UUID registrationId
    ) implements RegistrationCommand { }

    record RollbackPaid(
        String messageId,
        UUID registrationId,
        String reason
    ) implements RegistrationCommand { }

    record ReserveTicketAsync(
        String messageId,
        UUID campaignId,
        UUID userId,
        String providerCode
    ) implements RegistrationCommand { }
}
