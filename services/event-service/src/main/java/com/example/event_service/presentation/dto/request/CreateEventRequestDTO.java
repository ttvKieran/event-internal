package com.example.event_service.presentation.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateEventRequestDTO {
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
}
