// RegistrationConfirmedEventPayload.java
package com.example.registration_service.application.dto.message;

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
