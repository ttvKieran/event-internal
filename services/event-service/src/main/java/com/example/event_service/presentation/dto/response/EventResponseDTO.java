package com.example.event_service.presentation.dto.response;

import com.example.event_service.presentation.dto.EventResourceAllocationDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class EventResponseDTO {
    private UUID eventId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String ticketType;
    private Integer maxParticipants;
    private BigDecimal price;
    private String status;
    private LocalDateTime registrationOpenAt;
    private LocalDateTime registrationCloseAt;
    private LocalDateTime createdAt;
    private List<EventResourceAllocationDTO> allocatedResources;
}
