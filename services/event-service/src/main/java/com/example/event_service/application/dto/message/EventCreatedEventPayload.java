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
public class EventCreatedEventPayload {
    private UUID eventId;
    private String title;
    private LocalDateTime createdAt;
}
