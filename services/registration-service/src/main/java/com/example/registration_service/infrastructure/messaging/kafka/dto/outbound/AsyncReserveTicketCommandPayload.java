package com.example.registration_service.infrastructure.messaging.kafka.dto.outbound;

import java.util.UUID;

public record AsyncReserveTicketCommandPayload(
    UUID campaignId,
    UUID userId,
    ProviderPayload provider
) {
    public record ProviderPayload(String code) {
    }
}
