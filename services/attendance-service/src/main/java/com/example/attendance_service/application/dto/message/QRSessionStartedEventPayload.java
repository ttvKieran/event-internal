package com.example.attendance_service.application.dto.message;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class QRSessionStartedEventPayload {
    private String sessionId;
    private String eventId;
    private java.util.List<String> qrCode;
    private Instant startedAt;
    private Instant expiresAt;
}
