package com.example.registration_service.application.service;

import com.example.registration_service.application.model.command.PaymentResultCommand;
import com.example.registration_service.application.port.in.HandlePaymentResult;
import com.example.registration_service.application.port.out.ProcessedMessageStore;
import com.example.registration_service.application.port.out.SagaCommandPort;

import java.time.Clock;
import java.util.Objects;

public final class HandlePaymentResultInteractor
    implements HandlePaymentResult {

    private final ProcessedMessageStore processedMessages;
    private final SagaCommandPort sagaCommands;
    private final Clock clock;

    public HandlePaymentResultInteractor(ProcessedMessageStore processedMessages,
        SagaCommandPort sagaCommands, Clock clock) {
        this.processedMessages = Objects.requireNonNull(processedMessages);
        this.sagaCommands = Objects.requireNonNull(sagaCommands);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public void handle(PaymentResultCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        boolean firstDelivery = processedMessages.tryClaim(command.messageId(), clock.instant());
        if (!firstDelivery) return;

        switch (command) {
            case PaymentResultCommand.Succeeded succeeded ->
                sagaCommands.sendConfirmCommand(succeeded.registrationId(), succeeded.paymentId());

            case PaymentResultCommand.Failed failed ->
                sagaCommands.sendRollbackCommand(failed.registrationId(), failed.reason());
        }
    }
}
