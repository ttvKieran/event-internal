package com.example.attendance_service.presentation.dto.request;
import lombok.Data;

@Data
public class ScanQrRequest {
    private String eventId;
    private String employeeId;
    private String qrToken;
}
