// RegistrationConfirmedEventPayload.java
package com.example.registration_service.infrastructure.messaging.kafka.dto.outbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RegistrationConfirmedEventPayload {
    private UUID registrationId;
    private UUID campaignId;
    private UUID employeeId;
    private String confirmedAt;
}
