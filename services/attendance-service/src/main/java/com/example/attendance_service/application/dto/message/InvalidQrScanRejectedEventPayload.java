package com.example.attendance_service.application.dto.message;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class InvalidQrScanRejectedEventPayload {
    private String eventId;
    private String scannedToken;
    private String reason;
    private Instant attemptedAt;
}
