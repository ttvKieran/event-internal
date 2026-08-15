package com.example.event_service.application.dto.command;

import lombok.Value;
import java.time.LocalDateTime;
import java.util.UUID;
@Value
public class CreateEventResultDTO {
    UUID eventId;
    String status;
    LocalDateTime createdAt;
}
