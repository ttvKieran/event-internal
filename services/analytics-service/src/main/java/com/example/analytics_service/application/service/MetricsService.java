package com.example.analytics_service.application.service;
import com.example.analytics_service.application.port.in.QueryMetricsUseCase;
import com.example.analytics_service.application.port.in.UpdateMetricsUseCase;
import com.example.analytics_service.application.port.out.MetricsMessagePort;
import com.example.analytics_service.domain.model.aggregate.EventMetrics;
import com.example.analytics_service.domain.repository.EventMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MetricsService implements UpdateMetricsUseCase, QueryMetricsUseCase {
    private final EventMetricsRepository repository;
    private final MetricsMessagePort messagePort;

    @Override
    @Transactional
    public void processEventCreated(UUID eventId) {
        EventMetrics metrics = getOrInitialize(eventId);
        repository.save(metrics);
        messagePort.publishMetricsUpdatedEvent(metrics);
    }

    @Override
    @Transactional
    public void processEventDetailsConfigured(UUID eventId, String ticketType, int maxParticipants, BigDecimal price) {
        EventMetrics metrics = getOrInitialize(eventId);
        metrics.configureDetails(ticketType, maxParticipants, price);
        repository.save(metrics);
        messagePort.publishMetricsUpdatedEvent(metrics);
    }

    @Override
    @Transactional
    public void processRegistrationConfirmed(UUID eventId) {
        EventMetrics metrics = getOrInitialize(eventId);
        metrics.recordRegistrationConfirmed();
        repository.save(metrics);
        messagePort.publishMetricsUpdatedEvent(metrics);
    }

    @Override
    @Transactional
    public void processRegistrationRolledBack(UUID eventId) {
        EventMetrics metrics = getOrInitialize(eventId);
        metrics.recordRegistrationRolledBack();
        repository.save(metrics);
        messagePort.publishMetricsUpdatedEvent(metrics);
    }

    @Override
    @Transactional
    public void processParticipantCheckedIn(UUID eventId) {
        EventMetrics metrics = getOrInitialize(eventId);
        metrics.recordParticipantCheckedIn();
        repository.save(metrics);
        messagePort.publishMetricsUpdatedEvent(metrics);
    }

    @Override
    @Transactional(readOnly = true)
    public EventMetrics getMetrics(UUID eventId) {
        return getExistingMetrics(eventId);
    }

    private EventMetrics getOrInitialize(UUID eventId) {
        return repository.findById(eventId).orElseGet(() -> EventMetrics.builder()
                .eventId(eventId)
                .ticketType("FREE")
                .ticketPrice(BigDecimal.ZERO)
                .maxParticipants(0)
                .build());
    }
    
    private EventMetrics getExistingMetrics(UUID eventId) {
        return repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot found this event infomation"));
    }
}
