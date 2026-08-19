package com.example.registration_service.application.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDetailsConfiguredMessage {
    private UUID eventId;
    private String ticketType;
    private int maxParticipants;
    private java.math.BigDecimal price;
    private LocalDateTime registrationOpenAt;
    private LocalDateTime registrationCloseAt;
}
