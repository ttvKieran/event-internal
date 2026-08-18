package com.example.event_service.application.dto;

import com.example.event_service.domain.model.aggregate.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class EventDetailsDTO {
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
    private List<ResourceItemDTO> allocatedResources; // Bổ sung mảng Tài nguyên

    @Data
    @AllArgsConstructor
    public static class ResourceItemDTO {
        private UUID id;
        private UUID resourceId;
        private String note;
        private BigDecimal quantity;
    }

    public static EventDetailsDTO fromDomain(Event event) {
        if (event == null) return null;

        List<ResourceItemDTO> resourceList = null;
        if (event.getAllocatedResources() != null) {
            resourceList = event.getAllocatedResources().stream()
                .map(res -> new ResourceItemDTO(res.getId(), res.getResourceId(), res.getNote(), res.getQuantity()))
                .collect(Collectors.toList());
        }

        return new EventDetailsDTO(
            event.getEventId(),
            event.getTitle(),
            event.getDescription(),
            event.getSchedule() != null ? event.getSchedule().getStartTime() : null,
            event.getSchedule() != null ? event.getSchedule().getEndTime() : null,
            event.getLocation(),
            event.getTicketDetails() != null ? event.getTicketDetails().getType().getCode() : null,
            event.getTicketDetails() != null ? event.getTicketDetails().getMaxParticipants() : null,
            event.getTicketDetails() != null ? event.getTicketDetails().getPrice() : null,
            event.getStatus().getCode(),
            event.getSchedule() != null ? event.getSchedule().getRegistrationOpenAt() : null,
            event.getSchedule() != null ? event.getSchedule().getRegistrationCloseAt() : null,
            event.getCreatedAt(),
            resourceList
        );
    }
}
