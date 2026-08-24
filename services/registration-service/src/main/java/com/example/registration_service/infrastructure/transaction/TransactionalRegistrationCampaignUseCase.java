package com.example.registration_service.infrastructure.transaction;

import com.example.registration_service.application.model.result.CampaignStatusResult;
import com.example.registration_service.application.port.in.RegistrationCampaignUseCase;
import com.example.registration_service.application.port.in.RegistrationWindowUseCase;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class TransactionalRegistrationCampaignUseCase
    implements RegistrationCampaignUseCase, RegistrationWindowUseCase {

    private final RegistrationCampaignUseCase lifecycleDelegate;
    private final RegistrationWindowUseCase windowDelegate;
    private final TransactionTemplate transactionTemplate;

    public TransactionalRegistrationCampaignUseCase(
        RegistrationCampaignUseCase lifecycleDelegate,
        RegistrationWindowUseCase windowDelegate,
        TransactionTemplate transactionTemplate) {
        this.lifecycleDelegate = Objects.requireNonNull(
            lifecycleDelegate
        );
        this.windowDelegate = Objects.requireNonNull(windowDelegate);
        this.transactionTemplate =
            Objects.requireNonNull(transactionTemplate);
    }

    @Override
    public void createCampaignSnapshot(
        UUID campaignId,
        String ticketTypeCode,
        Integer maxParticipants,
        BigDecimal price,
        LocalDateTime openAt,
        LocalDateTime closeAt) {

        inTransaction(() ->
            lifecycleDelegate.createCampaignSnapshot(
                campaignId,
                ticketTypeCode,
                maxParticipants,
                price,
                openAt,
                closeAt
            )
        );
    }

    @Override
    public void activateCampaign(UUID campaignId) {
        inTransaction(() ->
            lifecycleDelegate.activateCampaign(campaignId)
        );
    }

    @Override
    public void cancelCampaign(UUID campaignId) {
        inTransaction(() ->
            lifecycleDelegate.cancelCampaign(campaignId)
        );
    }

    @Override
    public CampaignStatusResult openRegistration(UUID campaignId) {
        return inTransaction(() ->
            windowDelegate.openRegistration(campaignId)
        );
    }

    @Override
    public CampaignStatusResult closeRegistration(UUID campaignId) {
        return inTransaction(() ->
            windowDelegate.closeRegistration(campaignId)
        );
    }

    private void inTransaction(Runnable operation) {
        transactionTemplate.executeWithoutResult(
            status -> operation.run()
        );
    }

    private <T> T inTransaction(
        java.util.function.Supplier<T> operation) {
        return transactionTemplate.execute(
            status -> operation.get()
        );
    }
}
