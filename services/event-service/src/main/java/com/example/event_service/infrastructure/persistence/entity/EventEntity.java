package com.example.event_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "event")
@Getter
@Setter
public class EventEntity extends BaseEntity {

    @Id
    private UUID id;

    private String title;
    private String description;
    private String location;
    private String status;

    // TicketDetails
    private String ticketTypeCode;
    private Integer maxParticipants;
    private BigDecimal price;

    // EventSchedule
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime registrationStartTime;
    private LocalDateTime registrationEndTime;

    // Quan hệ với bảng Resources
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceAllocationEntity> resources = new ArrayList<>();
}
