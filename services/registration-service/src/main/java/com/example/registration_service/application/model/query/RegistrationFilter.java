package com.example.registration_service.application.model.query;

import java.util.Locale;
import java.util.UUID;

public record RegistrationFilter(
    UUID campaignId,
    UUID userId,
    String statusCode
) {
    public RegistrationFilter {
        statusCode = statusCode == null || statusCode.isBlank() ? null
            : statusCode.trim().toUpperCase(Locale.ROOT);
    }
}
