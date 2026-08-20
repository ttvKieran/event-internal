package com.example.event_service.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailsConfiguredEventPayload {
    private UUID eventId;
    private String ticketType; // Nhận 1 trong 2 giá trị: "FREE" hoặc "PAID"
    private List<ResourceAllocationMessage> allocatedResources;
    private Integer maxParticipants;
    private LocalDateTime registrationOpenAt;
    private LocalDateTime registrationCloseAt;
    private java.math.BigDecimal price;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceAllocationMessage {
        private UUID resourceId;
        private Double quantity;
    }
}
