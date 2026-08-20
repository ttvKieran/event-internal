package com.example.attendance_service.domain.repository;

public interface IRegistrationReadRepository {
    boolean isRegistered(String eventId, String employeeId);
}
