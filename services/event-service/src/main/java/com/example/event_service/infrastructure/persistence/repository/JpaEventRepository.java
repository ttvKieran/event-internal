package com.example.event_service.infrastructure.persistence.repository;

import com.example.event_service.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Chỉ có 1 dòng, Spring Data tự xử lý SQL
public interface JpaEventRepository extends JpaRepository<EventEntity, UUID> {
}
