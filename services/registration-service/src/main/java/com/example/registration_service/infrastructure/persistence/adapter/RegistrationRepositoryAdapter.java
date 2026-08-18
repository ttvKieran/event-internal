package com.example.registration_service.infrastructure.persistence.adapter;

import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.domain.model.valueobject.RegistrationStatus;
import com.example.registration_service.domain.repository.RegistrationRepository;
import com.example.registration_service.infrastructure.persistence.entity.RegistrationEntity;
import com.example.registration_service.infrastructure.persistence.mapper.RegistrationMapper;
import com.example.registration_service.infrastructure.persistence.repository.JpaRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationRepositoryAdapter implements RegistrationRepository {

    private final JpaRegistrationRepository jpaRepository;
    private final RegistrationMapper mapper;

    @Override
    public Optional<Registration> findById(UUID registrationId) {
        return jpaRepository.findById(registrationId)
            .map(mapper::toDomain);
    }

    @Override
    public void save(Registration registration) {
        RegistrationEntity entity = mapper.toEntity(registration);
        jpaRepository.save(entity);
    }

    @Override
    public List<Registration> findByFilters(UUID campaignId, UUID userId, RegistrationStatus status) {
        String statusCode = (status != null) ? status.getCode() : null;

        return jpaRepository.findByFilters(campaignId, userId, statusCode)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }
}
