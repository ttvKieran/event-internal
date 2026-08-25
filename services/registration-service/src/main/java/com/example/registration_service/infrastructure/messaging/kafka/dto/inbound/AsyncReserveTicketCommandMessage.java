package com.example.registration_service.infrastructure.messaging.kafka.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncReserveTicketCommandMessage {
    private UUID campaignId;
    private UUID userId;
    private ProviderMessage provider;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProviderMessage {
        private String code;
    }
}
