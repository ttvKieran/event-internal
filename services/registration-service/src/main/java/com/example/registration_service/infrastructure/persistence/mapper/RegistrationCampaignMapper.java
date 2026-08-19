package com.example.registration_service.infrastructure.persistence.mapper;

import com.example.registration_service.domain.model.aggregate.RegistrationCampaign;
import com.example.registration_service.domain.model.valueobject.CampaignStatus;
import com.example.registration_service.domain.model.valueobject.RegistrationTimeWindow;
import com.example.registration_service.domain.model.valueobject.TicketType;
import com.example.registration_service.infrastructure.persistence.entity.RegistrationCampaignEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistrationCampaignMapper {

    public RegistrationCampaignEntity toEntity(RegistrationCampaign domain) {
        if (domain == null) return null;

        RegistrationCampaignEntity entity = new RegistrationCampaignEntity();
        entity.setCampaignId(domain.getCampaignId());

        if (domain.getTicketType() != null) {
            entity.setTicketType(domain.getTicketType().getCode());
        }

        entity.setMaxParticipants(domain.getMaxParticipants());
        entity.setCurrentParticipants(domain.getCurrentParticipants());
        entity.setPrice(domain.getPrice());

        if (domain.getTimeWindow() != null) {
            entity.setOpenAt(domain.getTimeWindow().getOpenAt());
            entity.setCloseAt(domain.getTimeWindow().getCloseAt());
        }

        if (domain.getStatus() != null) {
            entity.setStatus(domain.getStatus().getCode());
        }

        return entity;
    }

    public RegistrationCampaign toDomain(RegistrationCampaignEntity entity) {
        if (entity == null) return null;

        RegistrationTimeWindow timeWindow = null;
        if (entity.getOpenAt() != null || entity.getCloseAt() != null) {
            timeWindow = RegistrationTimeWindow.of(entity.getOpenAt(), entity.getCloseAt());
        }

        return RegistrationCampaign.reconstitute(
            entity.getCampaignId(),
            entity.getTicketType() != null ? TicketType.of(entity.getTicketType()) : null,
            entity.getMaxParticipants(),
            entity.getCurrentParticipants(),
            entity.getPrice(),
            timeWindow,
            entity.getStatus() != null ? CampaignStatus.of(entity.getStatus()) : null
        );
    }
}
