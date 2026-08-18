package com.example.registration_service.domain.repository;

import com.example.registration_service.domain.model.aggregate.RegistrationCampaign;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationCampaignRepository {
    Optional<RegistrationCampaign> findById(UUID campaignId);
    void save(RegistrationCampaign campaign);
}
