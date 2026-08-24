package com.example.registration_service.application.model.command;

import java.util.Objects;
import java.util.UUID;

public record CancelRegistrationCommand(
    UUID registrationId,
    String reason
) {
    public CancelRegistrationCommand {
        Objects.requireNonNull(
            registrationId,
            "registrationId không được null"
        );

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                "Lý do hủy không được để trống"
            );
        }

        reason = reason.trim();
    }
}
