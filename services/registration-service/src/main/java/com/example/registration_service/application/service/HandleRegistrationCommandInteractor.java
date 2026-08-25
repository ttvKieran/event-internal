package com.example.registration_service.application.service;

import com.example.registration_service.application.model.command.CancelRegistrationCommand;
import com.example.registration_service.application.model.command.RegistrationCommand;
import com.example.registration_service.application.model.command.ReserveTicketCommand;
import com.example.registration_service.application.port.in.HandleRegistrationCommand;
import com.example.registration_service.application.port.in.RegistrationCommandUseCase;
import com.example.registration_service.application.port.out.ProcessedMessageStore;

import java.time.Clock;
import java.util.Objects;

public final class HandleRegistrationCommandInteractor
    implements HandleRegistrationCommand {

    private final ProcessedMessageStore processedMessages;
    private final RegistrationCommandUseCase registrations;
    private final Clock clock;

    public HandleRegistrationCommandInteractor(ProcessedMessageStore processedMessages,
        RegistrationCommandUseCase registrations, Clock clock) {
        this.processedMessages = Objects.requireNonNull(processedMessages);
        this.registrations = Objects.requireNonNull(registrations);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public void handle(RegistrationCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        boolean firstDelivery = processedMessages.tryClaim(command.messageId(), clock.instant());
        if (!firstDelivery) return;

        switch (command) {
            case RegistrationCommand.ConfirmPaid confirm ->
                registrations.confirmRegistration(confirm.registrationId());

            case RegistrationCommand.RollbackPaid rollback ->
                registrations.cancelRegistration(
                    new CancelRegistrationCommand(rollback.registrationId(), rollback.reason())
                );

            case RegistrationCommand.ReserveTicketAsync reserve ->
                registrations.processAsyncReservation(
                    new ReserveTicketCommand(
                        reserve.campaignId(),
                        reserve.userId(),
                        reserve.providerCode()
                    )
                );
        }
    }
}
