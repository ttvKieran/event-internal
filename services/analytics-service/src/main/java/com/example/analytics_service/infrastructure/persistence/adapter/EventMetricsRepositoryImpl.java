package com.example.analytics_service.infrastructure.persistence.adapter;
import com.example.analytics_service.domain.model.aggregate.EventMetrics;
import com.example.analytics_service.domain.repository.EventMetricsRepository;
import com.example.analytics_service.infrastructure.persistence.entity.EventMetricsEntity;
import com.example.analytics_service.infrastructure.persistence.repository.EventMetricsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventMetricsRepositoryImpl implements EventMetricsRepository {
    private final EventMetricsJpaRepository jpaRepo;

    @Override
    public Optional<EventMetrics> findById(UUID eventId) {
        return jpaRepo.findById(eventId).map(this::toDomain);
    }

    @Override
    public EventMetrics save(EventMetrics metrics) {
        EventMetricsEntity entity = new EventMetricsEntity();
        entity.setEventId(metrics.getEventId());
        entity.setTicketType(metrics.getTicketType());
        entity.setTicketPrice(metrics.getTicketPrice());
        entity.setMaxParticipants(metrics.getMaxParticipants());
        entity.setTotalRegistrations(metrics.getTotalRegistrations());
        entity.setFreeCount(metrics.getFreeCount());
        entity.setPaidCount(metrics.getPaidCount());
        entity.setCancelledCount(metrics.getCancelledCount());
        entity.setRevenue(metrics.getRevenue());
        entity.setCheckedInCount(metrics.getCheckedInCount());
        entity.setCheckInRate(metrics.getCheckInRate());
        entity.setFillRate(metrics.getFillRate());
        entity.setUpdatedAt(metrics.getUpdatedAt());
        jpaRepo.save(entity);
        return metrics;
    }

    private EventMetrics toDomain(EventMetricsEntity entity) {
        return EventMetrics.builder()
                .eventId(entity.getEventId())
                .ticketType(entity.getTicketType())
                .ticketPrice(entity.getTicketPrice())
                .maxParticipants(entity.getMaxParticipants())
                .totalRegistrations(entity.getTotalRegistrations())
                .freeCount(entity.getFreeCount())
                .paidCount(entity.getPaidCount())
                .cancelledCount(entity.getCancelledCount())
                .revenue(entity.getRevenue())
                .checkedInCount(entity.getCheckedInCount())
                .checkInRate(entity.getCheckInRate())
                .fillRate(entity.getFillRate())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
