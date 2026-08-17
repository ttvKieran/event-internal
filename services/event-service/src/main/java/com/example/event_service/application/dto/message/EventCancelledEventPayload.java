package com.example.event_service.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCancelledEventPayload {
    private UUID eventId;
    private String reason;
    private LocalDateTime cancelledAt;
}
