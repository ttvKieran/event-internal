package com.example.registration_service.application.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventPublishedMessage {
    private UUID eventId;
    private LocalDateTime publishedAt;
}
