package com.example.payment_service.infrastructure.persistence.repository;

import com.example.payment_service.infrastructure.persistence.entity.ProcessedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProcessedMessageRepository extends JpaRepository<ProcessedMessageEntity, String> {
}
