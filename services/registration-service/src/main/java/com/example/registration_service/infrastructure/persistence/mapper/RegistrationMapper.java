package com.example.registration_service.infrastructure.persistence.mapper;

import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.domain.model.valueobject.RegistrationStatus;
import com.example.registration_service.infrastructure.persistence.entity.RegistrationEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {

    public RegistrationEntity toEntity(Registration domain) {
        if (domain == null) return null;

        RegistrationEntity entity = new RegistrationEntity();
        entity.setRegistrationId(domain.getRegistrationId());
        entity.setCampaignId(domain.getCampaignId());
        entity.setUserId(domain.getUserId());

        if (domain.getStatus() != null) {
            entity.setStatus(domain.getStatus().getCode());
        }

        entity.setCancelReason(domain.getCancelReason());
        entity.setRegisteredAt(domain.getRegisteredAt());

        return entity;
    }

    public Registration toDomain(RegistrationEntity entity) {
        if (entity == null) return null;

        return Registration.reconstitute(
            entity.getRegistrationId(),
            entity.getCampaignId(),
            entity.getUserId(),
            entity.getStatus() != null ? RegistrationStatus.of(entity.getStatus()) : null,
            entity.getRegisteredAt(),
            entity.getCancelReason()
        );
    }
}
