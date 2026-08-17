package com.example.event_service.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EventPublishedEventPayload {
    private UUID eventId;
    private LocalDateTime publishedAt;
}
