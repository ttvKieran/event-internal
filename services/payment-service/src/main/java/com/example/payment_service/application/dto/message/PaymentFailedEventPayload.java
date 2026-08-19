package com.example.payment_service.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentFailedEventPayload {
    private UUID paymentId;
    private UUID registrationId;
    private UUID campaignId;
    private String reason;
    private String failedAt;
}
