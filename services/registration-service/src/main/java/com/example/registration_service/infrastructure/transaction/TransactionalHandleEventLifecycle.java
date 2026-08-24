package com.example.registration_service.infrastructure.transaction;

import com.example.registration_service.application.model.command.EventLifecycleCommand;
import com.example.registration_service.application.port.in.HandleEventLifecycle;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

public final class TransactionalHandleEventLifecycle
    implements HandleEventLifecycle {

    private final HandleEventLifecycle delegate;
    private final TransactionTemplate transactionTemplate;

    public TransactionalHandleEventLifecycle(
        HandleEventLifecycle delegate,
        TransactionTemplate transactionTemplate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.transactionTemplate =
            Objects.requireNonNull(transactionTemplate);
    }

    @Override
    public void handle(EventLifecycleCommand command) {
        transactionTemplate.executeWithoutResult(
            status -> delegate.handle(command)
        );
    }
}
