package com.example.attendance_service.infrastructure.persistence.entity;

import com.example.attendance_service.domain.model.valueobject.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "attendance_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecordEntity {
    @Id
    private UUID id;
    private UUID eventId;
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    private Instant scannedAt;
}
