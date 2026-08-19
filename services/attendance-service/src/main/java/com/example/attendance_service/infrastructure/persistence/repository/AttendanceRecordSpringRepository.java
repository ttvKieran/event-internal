package com.example.attendance_service.infrastructure.persistence.repository;

import com.example.attendance_service.infrastructure.persistence.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AttendanceRecordSpringRepository extends JpaRepository<AttendanceRecordEntity, UUID> {
    boolean existsByEventIdAndEmployeeId(UUID eventId, UUID employeeId);
}
