package com.example.registration_service.application.dto;

import com.example.registration_service.domain.model.valueobject.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveTicketDTO {
    private UUID campaignId;
    private UUID userId;
    private PaymentProvider provider;
}
