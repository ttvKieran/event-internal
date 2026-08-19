package com.example.attendance_service.application.port.in;

public interface ScanQrUseCase {
    String scanCheckIn(String eventId, String employeeId, String qrToken);
}
