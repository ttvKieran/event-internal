package com.example.registration_service.application.port.in;

import com.example.registration_service.application.model.command.CancelRegistrationCommand;
import com.example.registration_service.application.model.command.ReserveTicketCommand;
import com.example.registration_service.application.model.result.RegistrationResult;

import java.util.UUID;

public interface RegistrationCommandUseCase {
    UUID reserveTicket(ReserveTicketCommand command);

    void confirmRegistration(UUID registrationId);

    RegistrationResult cancelRegistration(
        CancelRegistrationCommand command
    );

    RegistrationResult rollbackPaidRegistration(
        CancelRegistrationCommand command
    );

    void reserveTicketAsync(ReserveTicketCommand command);

    void processAsyncReservation(ReserveTicketCommand command);
}
