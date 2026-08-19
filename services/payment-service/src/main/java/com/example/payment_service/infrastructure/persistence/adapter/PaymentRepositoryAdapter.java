package com.example.payment_service.infrastructure.persistence.adapter;

import com.example.payment_service.domain.model.aggregate.PaymentTransaction;
import com.example.payment_service.domain.repository.PaymentRepository;
import com.example.payment_service.infrastructure.persistence.mapper.PaymentMapper;
import com.example.payment_service.infrastructure.persistence.repository.JpaPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final JpaPaymentRepository jpaRepository;
    private final PaymentMapper mapper;

    @Override
    public void save(PaymentTransaction transaction) {
        jpaRepository.save(mapper.toEntity(transaction));
    }

    @Override
    public Optional<PaymentTransaction> findById(UUID paymentId) {
        return jpaRepository.findById(paymentId).map(mapper::toDomain);
    }

    @Override
    public Optional<PaymentTransaction> findByRegistrationId(UUID registrationId) {
        return jpaRepository.findByRegistrationId(registrationId).map(mapper::toDomain);
    }

    @Override
    public List<PaymentTransaction> findPendingBefore(LocalDateTime threshold) {
        return jpaRepository.findPendingBefore(threshold)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }
}
