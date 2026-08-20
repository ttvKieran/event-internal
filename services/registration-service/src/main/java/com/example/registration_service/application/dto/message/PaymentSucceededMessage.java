package com.example.registration_service.application.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSucceededMessage {
    private UUID paymentId;
    private UUID registrationId;
    private UUID campaignId;
    private BigDecimal amount;
    private String paidAt;
}
