package com.example.event_service.application.command;

import lombok.Value;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
public class ConfigureEventCommand {
    UUID eventId;
    String ticketTypeCode;
    Integer maxParticipants;
    BigDecimal price;
    List<ResourceItem> resources;

    @Value
    public static class ResourceItem {
        String resourceId;
        String note;
        BigDecimal quantity;
    }
}


