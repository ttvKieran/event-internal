package com.example.analytics_service.infrastructure.messaging.kafka;
import com.example.analytics_service.application.port.out.MetricsMessagePort;
import com.example.analytics_service.domain.model.aggregate.EventMetrics;
import com.example.analytics_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.example.analytics_service.infrastructure.persistence.repository.OutboxEventJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsMessageAdapter implements MetricsMessagePort {
    private final OutboxEventJpaRepository outboxRepo;
    private final ObjectMapper objectMapper;

    @Override
    public void publishMetricsUpdatedEvent(EventMetrics metrics) {
        try {
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("eventId", metrics.getEventId().toString());
            payloadMap.put("updatedAt", metrics.getUpdatedAt().toString());

            OutboxEventEntity event = new OutboxEventEntity(
                UUID.randomUUID(), 
                "Metrics", 
                metrics.getEventId().toString(),
                "MetricsUpdatedEvent", 
                UUID.randomUUID().toString(),
                objectMapper.writeValueAsString(payloadMap), 
                Instant.now()
            );
            outboxRepo.save(event);
        } catch (Exception e) {
            log.error("Error saving outbox event: ", e);
        }
    }
}
