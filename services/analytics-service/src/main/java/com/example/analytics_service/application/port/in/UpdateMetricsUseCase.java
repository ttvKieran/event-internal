package com.example.analytics_service.application.port.in;
import java.math.BigDecimal;
import java.util.UUID;
public interface UpdateMetricsUseCase {
    void processEventCreated(UUID eventId);
    void processEventDetailsConfigured(UUID eventId, String ticketType, int maxParticipants, BigDecimal price);
    void processRegistrationConfirmed(UUID eventId);
    void processRegistrationRolledBack(UUID eventId);
    void processParticipantCheckedIn(UUID eventId);
}
