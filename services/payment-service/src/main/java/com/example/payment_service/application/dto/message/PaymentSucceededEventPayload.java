package com.example.payment_service.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentSucceededEventPayload {
    private UUID paymentId;
    private UUID registrationId;
    private UUID campaignId;
    private BigDecimal amount;
    private String providerTxnId;
    private String paidAt;
}
