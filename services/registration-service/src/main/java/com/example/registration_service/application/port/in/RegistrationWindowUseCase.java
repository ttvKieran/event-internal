package com.example.registration_service.application.port.in;

import com.example.registration_service.application.model.result.CampaignStatusResult;

import java.util.UUID;

public interface RegistrationWindowUseCase {
    CampaignStatusResult openRegistration(UUID campaignId);

    CampaignStatusResult closeRegistration(UUID campaignId);
}
