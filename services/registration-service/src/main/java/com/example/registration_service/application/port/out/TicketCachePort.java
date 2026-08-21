package com.example.registration_service.application.port.out;

import java.util.UUID;

public interface TicketCachePort {
    void initializeTicketCache(UUID campaignId, int totalTickets);
    boolean decrementTicket(UUID campaignId);
    void setReservationStatus(UUID campaignId, UUID userId, String status);
    String getReservationStatus(UUID campaignId, UUID userId);
}
