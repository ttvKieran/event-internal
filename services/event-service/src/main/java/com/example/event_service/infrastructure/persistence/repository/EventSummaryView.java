package com.example.event_service.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EventSummaryView {
    UUID getId();
    String getTitle();
    String getStatus();
    String getTicketTypeCode();
    BigDecimal getPrice();
    LocalDateTime getStartTime();
}
