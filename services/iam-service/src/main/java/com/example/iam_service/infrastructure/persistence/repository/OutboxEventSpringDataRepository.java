package com.example.iam_service.infrastructure.persistence.repository;

import com.example.iam_service.infrastructure.persistence.entity.OutboxEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OutboxEventSpringDataRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
}
