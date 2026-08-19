package com.example.notification_service.application.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationConfirmedMessage {
    private UUID registrationId;
    private UUID campaignId;
    private UUID employeeId;
    private String confirmedAt;
}
