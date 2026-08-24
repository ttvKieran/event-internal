package com.example.registration_service.application.port.out;

import java.util.UUID;

@FunctionalInterface
public interface RegistrationIdGenerator {
    UUID nextId();
}
