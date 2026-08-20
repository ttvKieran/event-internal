package com.example.resource_service.infrastructure.persistence.repository;

import com.example.resource_service.infrastructure.persistence.entity.ResourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ResourceSpringDataRepository extends JpaRepository<ResourceJpaEntity, UUID> {
    List<ResourceJpaEntity> findByType(String type);
}
