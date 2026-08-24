package com.example.registration_service.infrastructure.messaging.kafka.dto.outbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RegistrationRequestedEventPayload {
    private UUID registrationId;
    private UUID campaignId;
    private UUID employeeId;
    private BigDecimal amount;
    private String provider;
    private String registeredAt;
}
