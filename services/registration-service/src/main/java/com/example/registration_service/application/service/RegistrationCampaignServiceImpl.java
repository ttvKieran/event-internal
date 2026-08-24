package com.example.registration_service.application.service;

import com.example.registration_service.application.model.result.CampaignStatusResult;
import com.example.registration_service.application.port.in.RegistrationCampaignUseCase;
import com.example.registration_service.application.port.in.RegistrationWindowUseCase;
import com.example.registration_service.application.port.out.RegistrationCampaignRepository;
import com.example.registration_service.application.port.out.TicketCachePort;
import com.example.registration_service.domain.model.aggregate.RegistrationCampaign;
import com.example.registration_service.domain.model.valueobject.CampaignStatus;
import com.example.registration_service.domain.model.valueobject.RegistrationTimeWindow;
import com.example.registration_service.domain.model.valueobject.TicketType;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class RegistrationCampaignServiceImpl
    implements RegistrationCampaignUseCase, RegistrationWindowUseCase {

    private final RegistrationCampaignRepository campaignRepository;
    private final TicketCachePort ticketCache;
    private final Clock clock;

    public RegistrationCampaignServiceImpl(
        RegistrationCampaignRepository campaignRepository,
        TicketCachePort ticketCache,
        Clock clock) {
        this.campaignRepository =
            Objects.requireNonNull(campaignRepository);
        this.ticketCache = Objects.requireNonNull(ticketCache);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public void createCampaignSnapshot(
        UUID campaignId,
        String ticketTypeCode,
        Integer maxParticipants,
        BigDecimal price,
        LocalDateTime openAt,
        LocalDateTime closeAt) {

        RegistrationTimeWindow timeWindow =
            RegistrationTimeWindow.of(openAt, closeAt);

        RegistrationCampaign campaign =
            RegistrationCampaign.createSnapshot(
                campaignId,
                TicketType.of(ticketTypeCode),
                maxParticipants,
                price,
                timeWindow
            );

        ticketCache.initializeTicketCache(
            campaignId,
            maxParticipants
        );

        campaignRepository.save(campaign);
    }

    @Override
    public void activateCampaign(UUID campaignId) {
        RegistrationCampaign campaign =
            campaignRepository.findById(campaignId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Không tìm thấy Chiến dịch bán vé"
                    )
                );

        campaign.changeStatus(CampaignStatus.ACTIVE);
        campaignRepository.save(campaign);
    }

    @Override
    public void cancelCampaign(UUID campaignId) {
        RegistrationCampaign campaign =
            campaignRepository.findById(campaignId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Không tìm thấy Chiến dịch bán vé"
                    )
                );

        campaign.changeStatus(CampaignStatus.CANCELLED);
        campaignRepository.save(campaign);
    }

    @Override
    public CampaignStatusResult openRegistration(UUID campaignId) {
        RegistrationCampaign campaign = getCampaign(campaignId);
        LocalDateTime openedAt = LocalDateTime.now(clock);

        campaign.openManually(openedAt);
        campaignRepository.save(campaign);

        return new CampaignStatusResult(
            campaignId,
            campaign.getStatus().getCode(),
            openedAt,
            null
        );
    }

    @Override
    public CampaignStatusResult closeRegistration(UUID campaignId) {
        RegistrationCampaign campaign = getCampaign(campaignId);
        LocalDateTime closedAt = LocalDateTime.now(clock);

        campaign.closeManually(closedAt);
        campaignRepository.save(campaign);

        return new CampaignStatusResult(
            campaignId,
            campaign.getStatus().getCode(),
            null,
            closedAt
        );
    }

    private RegistrationCampaign getCampaign(UUID campaignId) {
        return campaignRepository.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Không tìm thấy Chiến dịch bán vé"
            ));
    }
}
