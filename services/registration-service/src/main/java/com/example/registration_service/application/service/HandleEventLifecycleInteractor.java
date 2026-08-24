package com.example.registration_service.application.service;

import com.example.registration_service.application.model.command.EventLifecycleCommand;
import com.example.registration_service.application.port.in.HandleEventLifecycle;
import com.example.registration_service.application.port.in.RegistrationCampaignUseCase;
import com.example.registration_service.application.port.out.ProcessedMessageStore;

import java.time.Clock;
import java.util.Objects;

public final class HandleEventLifecycleInteractor
    implements HandleEventLifecycle {

    private final ProcessedMessageStore processedMessages;
    private final RegistrationCampaignUseCase campaigns;
    private final Clock clock;

    public HandleEventLifecycleInteractor(
        ProcessedMessageStore processedMessages,
        RegistrationCampaignUseCase campaigns,
        Clock clock) {
        this.processedMessages = Objects.requireNonNull(processedMessages);
        this.campaigns = Objects.requireNonNull(campaigns);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public void handle(EventLifecycleCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        boolean firstDelivery = processedMessages.tryClaim(
            command.messageId(),
            clock.instant()
        );

        if (!firstDelivery) {
            return;
        }

        switch (command) {
            case EventLifecycleCommand.ConfigureCampaign configure ->
                campaigns.createCampaignSnapshot(
                    configure.campaignId(),
                    configure.ticketTypeCode(),
                    configure.maxParticipants(),
                    configure.price(),
                    configure.openAt(),
                    configure.closeAt()
                );

            case EventLifecycleCommand.ActivateCampaign activate ->
                campaigns.activateCampaign(activate.campaignId());

            case EventLifecycleCommand.CancelCampaign cancel ->
                campaigns.cancelCampaign(cancel.campaignId());
        }
    }
}
