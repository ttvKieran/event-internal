package com.example.registration_service.application.port.in;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RegistrationCampaignUseCase {
    void createCampaignSnapshot(UUID campaignId, String ticketTypeCode,
                                Integer maxParticipants, LocalDateTime openAt, LocalDateTime closeAt);
    void activateCampaign(UUID campaignId);
    void cancelCampaign(UUID campaignId);
}
