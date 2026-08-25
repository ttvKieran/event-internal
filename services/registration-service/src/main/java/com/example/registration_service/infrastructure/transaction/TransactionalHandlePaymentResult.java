package com.example.registration_service.infrastructure.transaction;

import com.example.registration_service.application.model.command.PaymentResultCommand;
import com.example.registration_service.application.port.in.HandlePaymentResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

public final class TransactionalHandlePaymentResult implements HandlePaymentResult {

    private final HandlePaymentResult delegate;
    private final TransactionTemplate transactionTemplate;

    public TransactionalHandlePaymentResult(
        HandlePaymentResult delegate, TransactionTemplate transactionTemplate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate);
    }

    @Override
    public void handle(PaymentResultCommand command) {
        transactionTemplate.executeWithoutResult(
            status -> delegate.handle(command)
        );
    }
}
