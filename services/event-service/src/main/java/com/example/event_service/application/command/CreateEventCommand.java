package com.example.event_service.application.command;

import lombok.Value;
import java.time.LocalDateTime;

@Value
public class CreateEventCommand {
    String title;
    String description;
    String location;
    LocalDateTime startTime;
    LocalDateTime endTime;
}

