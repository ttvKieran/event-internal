package com.example.attendance_service.infrastructure.persistence.repository;
import com.example.attendance_service.infrastructure.persistence.entity.RegistrationReadModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface RegistrationReadModelSpringRepository extends JpaRepository<RegistrationReadModelEntity, UUID> {
    boolean existsByEventIdAndEmployeeIdAndStatus(UUID eventId, UUID employeeId, String status);
}
