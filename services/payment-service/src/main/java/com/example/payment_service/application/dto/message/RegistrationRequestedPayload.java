package com.example.payment_service.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestedPayload {
    private UUID registrationId;
    private UUID campaignId;
    private UUID employeeId;
    private BigDecimal amount;
    private String provider;
}
