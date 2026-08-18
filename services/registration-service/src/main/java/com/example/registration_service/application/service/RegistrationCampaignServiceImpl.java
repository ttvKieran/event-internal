package com.example.registration_service.application.service;

import com.example.registration_service.application.port.in.RegistrationCampaignUseCase;
import com.example.registration_service.domain.model.aggregate.RegistrationCampaign;
import com.example.registration_service.domain.model.valueobject.CampaignStatus;
import com.example.registration_service.domain.model.valueobject.RegistrationTimeWindow;
import com.example.registration_service.domain.model.valueobject.TicketType;
import com.example.registration_service.domain.repository.RegistrationCampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationCampaignServiceImpl implements RegistrationCampaignUseCase {

    private final RegistrationCampaignRepository campaignRepo;

    @Override
    @Transactional
    public void createCampaignSnapshot(UUID campaignId, String ticketTypeCode,
                                       Integer maxParticipants, LocalDateTime openAt, LocalDateTime closeAt) {
        RegistrationTimeWindow timeWindow = RegistrationTimeWindow.of(openAt, closeAt);
        RegistrationCampaign campaign = RegistrationCampaign.createSnapshot(
            campaignId, TicketType.of(ticketTypeCode), maxParticipants, timeWindow
        );
        campaignRepo.save(campaign);
    }

    @Override
    @Transactional
    public void activateCampaign(UUID campaignId) {
        RegistrationCampaign campaign = campaignRepo.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Chiến dịch bán vé"));
        campaign.changeStatus(CampaignStatus.ACTIVE);
        campaignRepo.save(campaign);
    }

    @Override
    @Transactional
    public void cancelCampaign(UUID campaignId) {
        RegistrationCampaign campaign = campaignRepo.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Chiến dịch bán vé"));
        campaign.changeStatus(CampaignStatus.CANCELLED);
        campaignRepo.save(campaign);
    }
}
