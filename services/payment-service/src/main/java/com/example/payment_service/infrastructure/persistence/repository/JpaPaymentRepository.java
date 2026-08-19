package com.example.payment_service.infrastructure.persistence.repository;

import com.example.payment_service.infrastructure.persistence.entity.PaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPaymentRepository extends JpaRepository<PaymentTransactionEntity, UUID> {

    Optional<PaymentTransactionEntity> findByRegistrationId(UUID registrationId);

    // Dùng cho Scheduler: Quét PENDING quá hạn
    @Query("SELECT p FROM PaymentTransactionEntity p WHERE p.status = 'PENDING' AND p.createdAt < :threshold")
    List<PaymentTransactionEntity> findPendingBefore(@Param("threshold") LocalDateTime threshold);
}
