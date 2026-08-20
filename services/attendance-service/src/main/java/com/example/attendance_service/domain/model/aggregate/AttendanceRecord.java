package com.example.attendance_service.domain.model.aggregate;
import com.example.attendance_service.domain.model.valueobject.AttendanceStatus;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter @Builder
public class AttendanceRecord {
    private String id;
    private String eventId;
    private String employeeId;
    private AttendanceStatus status;
    private Instant scannedAt;
}
