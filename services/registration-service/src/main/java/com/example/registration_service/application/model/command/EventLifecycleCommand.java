package com.example.registration_service.application.model.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface EventLifecycleCommand {

    String messageId();

    record ConfigureCampaign(
        String messageId,
        UUID campaignId,
        String ticketTypeCode,
        int maxParticipants,
        BigDecimal price,
        LocalDateTime openAt,
        LocalDateTime closeAt
    ) implements EventLifecycleCommand {
    }

    record ActivateCampaign(
        String messageId,
        UUID campaignId
    ) implements EventLifecycleCommand {
    }

    record CancelCampaign(
        String messageId,
        UUID campaignId
    ) implements EventLifecycleCommand {
    }
}
