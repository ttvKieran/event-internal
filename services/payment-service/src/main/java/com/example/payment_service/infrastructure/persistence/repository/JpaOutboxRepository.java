package com.example.payment_service.infrastructure.persistence.repository;

import com.example.payment_service.infrastructure.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaOutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {
}
