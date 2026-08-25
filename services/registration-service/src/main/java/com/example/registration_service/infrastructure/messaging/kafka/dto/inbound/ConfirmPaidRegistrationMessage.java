package com.example.registration_service.infrastructure.messaging.kafka.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfirmPaidRegistrationMessage {
    private UUID registrationId;
    private UUID paymentId;
}
