package com.example.registration_service.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaidRegistrationRolledBackEventPayload {
    private UUID registrationId;
    private UUID campaignId;
    private String reason;
    private String rolledBackAt;
}
