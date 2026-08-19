package com.example.registration_service.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReserveTicketRequest {
    @NotNull(message = "ID Chiến dịch không được để trống")
    private UUID campaignId;
    private String provider;
}
