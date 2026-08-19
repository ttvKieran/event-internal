package com.example.attendance_service.application.dto.message;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class DuplicateCheckInRejectedEventPayload {
    private String eventId;
    private String employeeId;
    private Instant attemptedAt;
}
