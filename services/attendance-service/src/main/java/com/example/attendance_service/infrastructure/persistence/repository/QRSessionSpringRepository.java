package com.example.attendance_service.infrastructure.persistence.repository;
import com.example.attendance_service.infrastructure.persistence.entity.QRSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface QRSessionSpringRepository extends JpaRepository<QRSessionEntity, UUID> {
    QRSessionEntity findFirstByEventIdOrderByExpiresAtDesc(UUID eventId);
}
