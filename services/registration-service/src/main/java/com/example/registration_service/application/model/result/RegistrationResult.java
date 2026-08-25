package com.example.registration_service.application.model.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegistrationResult(
    UUID registrationId,
    UUID campaignId,
    UUID userId,
    String status,
    String cancelReason,
    LocalDateTime registeredAt
) { }
