package com.example.event_service.application.dto;

import lombok.Value;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
public class ConfigureEventDTO {
    String ticketTypeCode;
    Integer maxParticipants;
    BigDecimal price;
    LocalDateTime registrationOpenAt;
    LocalDateTime registrationCloseAt;
    List<ResourceItem> resources;

    @Value
    public static class ResourceItem {
        UUID resourceId;
        String note;
        Double quantity;
    }
}
