package com.example.event_service.infrastructure.persistence.repository;

import com.example.event_service.domain.model.valueobject.EventStatus;
import com.example.event_service.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface JpaEventRepository extends JpaRepository<EventEntity, UUID> {
    @Query("SELECT e FROM EventEntity e WHERE (:status IS NULL OR e.status = :status)")
    Page<EventEntity> findByStatusWithPagination(
        @Param("status") String status,
        Pageable pageable
    );
}
