package com.example.registration_service.application.model.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record CampaignStatusResult(
    UUID campaignId,
    String status,
    LocalDateTime openedAt,
    LocalDateTime closedAt
) { }
