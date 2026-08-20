package com.example.registration_service.infrastructure.persistence.adapter;

import com.example.registration_service.domain.model.aggregate.RegistrationCampaign;
import com.example.registration_service.domain.repository.RegistrationCampaignRepository;
import com.example.registration_service.infrastructure.persistence.entity.RegistrationCampaignEntity;
import com.example.registration_service.infrastructure.persistence.mapper.RegistrationCampaignMapper;
import com.example.registration_service.infrastructure.persistence.repository.JpaRegistrationCampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationCampaignRepositoryAdapter implements RegistrationCampaignRepository {

    private final JpaRegistrationCampaignRepository jpaRepository;
    private final RegistrationCampaignMapper mapper;

    @Override
    public Optional<RegistrationCampaign> findById(UUID campaignId) {
        return jpaRepository.findById(campaignId)
            .map(mapper::toDomain);
    }

    @Override
    public void save(RegistrationCampaign campaign) {
        RegistrationCampaignEntity entity = mapper.toEntity(campaign);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<RegistrationCampaign> findByIdForUpdate(UUID campaignId) {
        return jpaRepository.findByIdForUpdate(campaignId)
            .map(mapper::toDomain);
    }
}
