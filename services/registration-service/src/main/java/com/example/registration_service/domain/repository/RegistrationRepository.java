package com.example.registration_service.domain.repository;

import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.domain.model.valueobject.RegistrationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationRepository {
    Optional<Registration> findById(UUID registrationId);
    void save(Registration registration);
    List<Registration> findByFilters(UUID campaignId, UUID userId, RegistrationStatus status);
}
