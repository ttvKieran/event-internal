package com.example.event_service.application.dto;

import lombok.Value;
import java.time.LocalDateTime;

@Value
public class CreateEventDTO {
    String title;
    String description;
    String location;
    LocalDateTime startTime;
    LocalDateTime endTime;
}
