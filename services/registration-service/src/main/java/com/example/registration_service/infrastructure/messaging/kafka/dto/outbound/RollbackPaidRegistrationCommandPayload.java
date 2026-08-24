package com.example.registration_service.infrastructure.messaging.kafka.dto.outbound;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;
@Data
@AllArgsConstructor
public class RollbackPaidRegistrationCommandPayload {
    private UUID registrationId;
    private String reason;
}
