package com.example.notification_service.infrastructure.persistence.repository;

import com.example.notification_service.infrastructure.persistence.entity.ProcessedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProcessedMessageRepository extends JpaRepository<ProcessedMessageEntity, String> {
}
