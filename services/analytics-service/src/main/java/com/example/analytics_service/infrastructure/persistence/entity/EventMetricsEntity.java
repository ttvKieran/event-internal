package com.example.analytics_service.infrastructure.persistence.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMetricsEntity {
    @Id
    private UUID eventId;
    private String ticketType;
    private BigDecimal ticketPrice;
    private int maxParticipants;
    private int totalRegistrations;
    private int freeCount;
    private int paidCount;
    private int cancelledCount;
    private BigDecimal revenue;
    private int checkedInCount;
    private double checkInRate;
    private double fillRate;
    private LocalDateTime updatedAt;
}
