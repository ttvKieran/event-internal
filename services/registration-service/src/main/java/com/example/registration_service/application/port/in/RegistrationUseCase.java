package com.example.registration_service.application.port.in;

import com.example.registration_service.application.dto.ReserveTicketDTO;
import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.domain.model.valueobject.RegistrationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RegistrationUseCase {
    UUID reserveTicket(ReserveTicketDTO command);
    void confirmRegistration(UUID registrationId);
    void cancelRegistration(UUID registrationId, String reason);
    void openRegistration(UUID campaignId);
    void closeRegistration(UUID campaignId);
    Registration getRegistrationById(UUID registrationId);
    List<Registration> getRegistrations(UUID campaignId, UUID userId, RegistrationStatus status);
    int countActiveRegistrations(UUID campaignId);
    void reserveTicketAsync(ReserveTicketDTO command);
    String getReservationStatus(UUID campaignId, UUID userId);
    void processAsyncReservation(ReserveTicketDTO command);
}
