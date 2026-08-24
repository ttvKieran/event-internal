package com.example.registration_service.application.port.in;

import com.example.registration_service.application.model.query.RegistrationFilter;
import com.example.registration_service.application.model.result.RegistrationResult;

import java.util.List;
import java.util.UUID;

public interface RegistrationQueryUseCase {
    RegistrationResult getRegistrationById(UUID registrationId);

    List<RegistrationResult> getRegistrations(
        RegistrationFilter filter
    );

    int countActiveRegistrations(UUID campaignId);

    String getReservationStatus(UUID campaignId, UUID userId);
}
