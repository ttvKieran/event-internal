package com.example.registration_service.infrastructure.transaction;

import com.example.registration_service.application.model.command.CancelRegistrationCommand;
import com.example.registration_service.application.model.command.ReserveTicketCommand;
import com.example.registration_service.application.model.query.RegistrationFilter;
import com.example.registration_service.application.model.result.RegistrationResult;
import com.example.registration_service.application.port.in.RegistrationCommandUseCase;
import com.example.registration_service.application.port.in.RegistrationQueryUseCase;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class TransactionalRegistrationUseCase
    implements RegistrationCommandUseCase, RegistrationQueryUseCase {

    private final RegistrationCommandUseCase commandDelegate;
    private final RegistrationQueryUseCase queryDelegate;
    private final TransactionTemplate writeTransactions;
    private final TransactionTemplate readTransactions;

    public TransactionalRegistrationUseCase(RegistrationCommandUseCase commandDelegate,
        RegistrationQueryUseCase queryDelegate, TransactionTemplate writeTransactions) {
        this.commandDelegate = Objects.requireNonNull(commandDelegate);
        this.queryDelegate = Objects.requireNonNull(queryDelegate);
        this.writeTransactions = Objects.requireNonNull(writeTransactions);

        PlatformTransactionManager transactionManager = Objects.requireNonNull(writeTransactions.getTransactionManager());

        this.readTransactions = new TransactionTemplate(transactionManager);
        this.readTransactions.setReadOnly(true);
    }

    @Override
    public UUID reserveTicket(ReserveTicketCommand command) {
        return writeTransactions.execute(
            status -> commandDelegate.reserveTicket(command)
        );
    }

    @Override
    public void confirmRegistration(UUID registrationId) {
        writeTransactions.executeWithoutResult(
            status -> commandDelegate.confirmRegistration(registrationId)
        );
    }

    @Override
    public RegistrationResult cancelRegistration(CancelRegistrationCommand command) {
        return writeTransactions.execute(
            status -> commandDelegate.cancelRegistration(command)
        );
    }

    @Override
    public void reserveTicketAsync(ReserveTicketCommand command) {
        writeTransactions.executeWithoutResult(
            status -> commandDelegate.reserveTicketAsync(command)
        );
    }

    @Override
    public void processAsyncReservation(ReserveTicketCommand command) {
        writeTransactions.executeWithoutResult(
            status -> commandDelegate.processAsyncReservation(command)
        );
    }

    @Override
    public RegistrationResult getRegistrationById(UUID registrationId) {
        return readTransactions.execute(
            status -> queryDelegate.getRegistrationById(registrationId)
        );
    }

    @Override
    public List<RegistrationResult> getRegistrations(RegistrationFilter filter) {
        return readTransactions.execute(
            status -> queryDelegate.getRegistrations(filter));
    }

    @Override
    public int countActiveRegistrations(UUID campaignId) {
        return readTransactions.execute(
            status -> queryDelegate.countActiveRegistrations(campaignId)
        );
    }

    @Override
    public String getReservationStatus(UUID campaignId, UUID userId) {
        return queryDelegate.getReservationStatus(campaignId, userId);
    }

}
