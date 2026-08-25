package com.example.registration_service.infrastructure.transaction;

import com.example.registration_service.application.model.result.CampaignStatusResult;
import com.example.registration_service.application.port.in.RegistrationCampaignUseCase;
import com.example.registration_service.application.port.in.RegistrationWindowUseCase;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public final class TransactionalRegistrationCampaignUseCase
    implements RegistrationCampaignUseCase, RegistrationWindowUseCase {

    private final RegistrationCampaignUseCase lifecycleDelegate;
    private final RegistrationWindowUseCase windowDelegate;
    private final TransactionTemplate transactionTemplate;

    public TransactionalRegistrationCampaignUseCase(RegistrationCampaignUseCase lifecycleDelegate,
        RegistrationWindowUseCase windowDelegate, TransactionTemplate transactionTemplate) {
        this.lifecycleDelegate = Objects.requireNonNull(lifecycleDelegate);
        this.windowDelegate = Objects.requireNonNull(windowDelegate);
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate);
    }

    @Override
    public void createCampaignSnapshot(UUID campaignId, String ticketTypeCode,
        Integer maxParticipants, BigDecimal price, LocalDateTime openAt, LocalDateTime closeAt) {
        transactionTemplate.executeWithoutResult(
            status -> lifecycleDelegate.createCampaignSnapshot(
                    campaignId, ticketTypeCode, maxParticipants, price, openAt, closeAt)
        );
    }

    @Override
    public void activateCampaign(UUID campaignId) {
        transactionTemplate.executeWithoutResult(
            status -> lifecycleDelegate.activateCampaign(campaignId)
        );
    }

    @Override
    public void cancelCampaign(UUID campaignId) {
        transactionTemplate.executeWithoutResult(
            status -> lifecycleDelegate.cancelCampaign(campaignId)
        );
    }

    @Override
    public CampaignStatusResult openRegistration(UUID campaignId) {
        return transactionTemplate.execute(
            status -> windowDelegate.openRegistration(campaignId)
        );
    }

    @Override
    public CampaignStatusResult closeRegistration(UUID campaignId) {
        return transactionTemplate.execute(
            status -> windowDelegate.closeRegistration(campaignId)
        );
    }
}
