package com.example.analytics_service.domain.model.aggregate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder
public class EventMetrics {
    private UUID eventId;

    private String ticketType;
    private BigDecimal ticketPrice;
    private int maxParticipants;

    @Builder.Default private int totalRegistrations = 0;
    @Builder.Default private int freeCount = 0;
    @Builder.Default private int paidCount = 0;
    @Builder.Default private int cancelledCount = 0;
    @Builder.Default private BigDecimal revenue = BigDecimal.ZERO;
    @Builder.Default private int checkedInCount = 0;
    @Builder.Default private double checkInRate = 0.0;
    @Builder.Default private double fillRate = 0.0;
    @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();

    public void configureDetails(String ticketType, int maxParticipants, BigDecimal ticketPrice) {
        this.ticketType = ticketType;
        this.maxParticipants = maxParticipants;
        this.ticketPrice = ticketPrice != null ? ticketPrice : BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
        recalculateRates();
    }

    public void recordRegistrationConfirmed() {
        this.totalRegistrations++;
        if ("FREE".equals(this.ticketType)) {
            this.freeCount++;
        } else if ("PAID".equals(this.ticketType)) {
            this.paidCount++;
            this.revenue = this.revenue.add(this.ticketPrice);
        }
        this.updatedAt = LocalDateTime.now();
        recalculateRates();
    }

    public void recordRegistrationRolledBack() {
        this.cancelledCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordParticipantCheckedIn() {
        this.checkedInCount++;
        this.updatedAt = LocalDateTime.now();
        recalculateRates();
    }

    private void recalculateRates() {
        if (this.totalRegistrations <= 0) {
            this.checkInRate = 0.0;
        } else {
            this.checkInRate = Math.round((((double) this.checkedInCount / this.totalRegistrations) * 100.0) * 100.0) / 100.0;
        }
        if (this.maxParticipants <= 0) {
            this.fillRate = 0.0;
        } else {
            this.fillRate = Math.round((((double) this.totalRegistrations / this.maxParticipants) * 100.0) * 100.0) / 100.0;
        }
    }
}
