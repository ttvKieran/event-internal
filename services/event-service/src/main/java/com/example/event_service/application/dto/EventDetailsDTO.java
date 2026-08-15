package com.example.event_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EventDetailsDTO {
    private UUID eventId;
    private String title;
    private String description;
    private String status;
    private String ticketType;
    private BigDecimal price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
}
