package com.example.registration_service.application.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequestedEventPayload {
    private UUID registrationId;
    private UUID campaignId;
    private UUID employeeId;
    private java.math.BigDecimal amount;
    private String provider;
    private String registeredAt;
}
