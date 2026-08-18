package com.example.registration_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CampaignStatusResponse {
    private UUID eventId;
    private String registrationStatus;
    private LocalDateTime opensAt;
    private LocalDateTime closedAt;
}
