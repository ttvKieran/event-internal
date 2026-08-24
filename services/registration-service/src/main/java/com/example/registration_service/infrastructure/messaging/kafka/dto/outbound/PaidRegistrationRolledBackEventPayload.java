package com.example.registration_service.infrastructure.messaging.kafka.dto.outbound;

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
