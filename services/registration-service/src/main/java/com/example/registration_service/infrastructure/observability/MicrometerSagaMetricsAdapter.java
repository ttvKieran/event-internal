package com.example.registration_service.infrastructure.observability;

import com.example.registration_service.application.port.out.SagaMetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class MicrometerSagaMetricsAdapter implements SagaMetricsPort {

    private final Counter successCounter;
    private final Counter failedRollbackCounter;
    private final Counter timeoutRollbackCounter;

    public MicrometerSagaMetricsAdapter(MeterRegistry registry) {
        this.successCounter = Counter.builder("registration.saga.outcomes")
            .description("Kết quả xử lý payment của registration saga")
            .tag("outcome", "success")
            .tag("reason", "none")
            .register(registry);

        this.failedRollbackCounter = Counter.builder("registration.saga.outcomes")
            .description("Kết quả xử lý payment của registration saga")
            .tag("outcome", "rollback")
            .tag("reason", "payment_failed")
            .register(registry);

        this.timeoutRollbackCounter = Counter.builder("registration.saga.outcomes")
            .description("Kết quả xử lý payment của registration saga")
            .tag("outcome", "rollback")
            .tag("reason", "timeout")
            .register(registry);
    }

    @Override
    public void recordPaymentSucceeded() {
        afterCommit(successCounter::increment);
    }

    @Override
    public void recordPaymentRolledBack(String reason) {
        Counter counter = "PAYMENT_TIMED_OUT".equals(reason)
            ? timeoutRollbackCounter
            : failedRollbackCounter;

        afterCommit(counter::increment);
    }

    private void afterCommit(Runnable operation) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            operation.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    operation.run();
                }
            }
        );
    }
}
