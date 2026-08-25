package com.example.registration_service.application.model.command;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public record ReserveTicketCommand(
    UUID campaignId,
    UUID userId,
    String providerCode
) {
    public ReserveTicketCommand {
        Objects.requireNonNull(campaignId, "campaignId không được null");
        Objects.requireNonNull(userId, "userId không được null");
        providerCode = providerCode == null || providerCode.isBlank() ? null
            : providerCode.trim().toUpperCase(Locale.ROOT);
    }
}
