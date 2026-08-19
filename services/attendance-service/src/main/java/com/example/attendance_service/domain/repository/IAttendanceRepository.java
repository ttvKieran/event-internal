package com.example.attendance_service.domain.repository;

import com.example.attendance_service.domain.model.aggregate.AttendanceRecord;

public interface IAttendanceRepository {
    AttendanceRecord save(AttendanceRecord record);
    boolean hasCheckedIn(String eventId, String employeeId);
}
