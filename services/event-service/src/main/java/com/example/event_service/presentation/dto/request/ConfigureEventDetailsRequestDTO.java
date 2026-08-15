package com.example.event_service.presentation.dto.request;

import com.example.event_service.presentation.dto.EventResourceAllocationDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConfigureEventDetailsRequestDTO {
    private String ticketType;
    private Integer maxParticipants;
    private BigDecimal price;
    private LocalDateTime registrationOpenAt;
    private LocalDateTime registrationCloseAt;
    private List<EventResourceAllocationDTO> allocatedResources;
}
