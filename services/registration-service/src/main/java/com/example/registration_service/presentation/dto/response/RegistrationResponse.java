package com.example.registration_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationResponse {
    private UUID registrationId;
    private UUID eventId;
    private UUID employeeId;
    private String status;
    private String cancelReason;
    private LocalDateTime registeredAt;
}
