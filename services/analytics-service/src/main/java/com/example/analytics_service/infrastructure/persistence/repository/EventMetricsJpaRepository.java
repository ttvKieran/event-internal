package com.example.analytics_service.infrastructure.persistence.repository;

import com.example.analytics_service.infrastructure.persistence.entity.EventMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface EventMetricsJpaRepository extends JpaRepository<EventMetricsEntity, UUID> {}
