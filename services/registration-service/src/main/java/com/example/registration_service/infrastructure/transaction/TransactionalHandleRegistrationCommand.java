package com.example.registration_service.infrastructure.transaction;

import com.example.registration_service.application.model.command.RegistrationCommand;
import com.example.registration_service.application.port.in.HandleRegistrationCommand;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

public final class TransactionalHandleRegistrationCommand
    implements HandleRegistrationCommand {

    private final HandleRegistrationCommand delegate;
    private final TransactionTemplate transactionTemplate;

    public TransactionalHandleRegistrationCommand(
        HandleRegistrationCommand delegate,
        TransactionTemplate transactionTemplate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.transactionTemplate =
            Objects.requireNonNull(transactionTemplate);
    }

    @Override
    public void handle(RegistrationCommand command) {
        transactionTemplate.executeWithoutResult(
            status -> delegate.handle(command)
        );
    }
}
