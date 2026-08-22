package com.example.analytics_service.infrastructure.persistence.repository;

import com.example.analytics_service.infrastructure.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {}
