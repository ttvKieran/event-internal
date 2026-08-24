package com.example.registration_service.application.port.in;

import com.example.registration_service.application.model.command.RegistrationCommand;

@FunctionalInterface
public interface HandleRegistrationCommand {

    void handle(RegistrationCommand command);
}
