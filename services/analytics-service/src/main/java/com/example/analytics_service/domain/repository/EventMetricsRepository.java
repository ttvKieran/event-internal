package com.example.analytics_service.domain.repository;
import com.example.analytics_service.domain.model.aggregate.EventMetrics;
import java.util.Optional;
import java.util.UUID;
public interface EventMetricsRepository {
    Optional<EventMetrics> findById(UUID eventId);
    EventMetrics save(EventMetrics metrics);
}
