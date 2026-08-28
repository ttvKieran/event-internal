package com.example.registration_service.application.port.out;

public interface SagaMetricsPort {
    void recordPaymentSucceeded();

    void recordPaymentRolledBack(String reason);
}
