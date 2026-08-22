package com.example.analytics_service.application.port.in;
import com.example.analytics_service.domain.model.aggregate.EventMetrics;
import java.util.UUID;
public interface QueryMetricsUseCase {
    EventMetrics getMetrics(UUID eventId);
}
