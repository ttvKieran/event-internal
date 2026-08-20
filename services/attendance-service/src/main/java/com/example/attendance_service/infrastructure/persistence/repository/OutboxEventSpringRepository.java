package com.example.attendance_service.infrastructure.persistence.repository;
import com.example.attendance_service.infrastructure.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface OutboxEventSpringRepository extends JpaRepository<OutboxEventEntity, UUID> {}
