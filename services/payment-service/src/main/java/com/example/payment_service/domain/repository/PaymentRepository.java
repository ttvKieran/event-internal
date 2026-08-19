package com.example.payment_service.domain.repository;

import com.example.payment_service.domain.model.aggregate.PaymentTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    void save(PaymentTransaction transaction);

    Optional<PaymentTransaction> findById(UUID paymentId);

    Optional<PaymentTransaction> findByRegistrationId(UUID registrationId);

    List<PaymentTransaction> findPendingBefore(LocalDateTime threshold);
}
