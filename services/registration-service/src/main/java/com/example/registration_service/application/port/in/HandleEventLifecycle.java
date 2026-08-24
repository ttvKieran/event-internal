package com.example.registration_service.application.port.in;

import com.example.registration_service.application.model.command.EventLifecycleCommand;

@FunctionalInterface
public interface HandleEventLifecycle {

    void handle(EventLifecycleCommand command);
}
