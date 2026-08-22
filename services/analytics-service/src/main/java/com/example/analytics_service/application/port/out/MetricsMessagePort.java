package com.example.analytics_service.application.port.out;
import com.example.analytics_service.domain.model.aggregate.EventMetrics;
public interface MetricsMessagePort {
    void publishMetricsUpdatedEvent(EventMetrics metrics);
}
