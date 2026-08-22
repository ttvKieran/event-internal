package com.example.analytics_service.presentation.dto.response;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data @Builder
public class AttendanceMetricsResponse {
    private UUID eventId;
    private int checkedInCount;
    private double checkInRate;
    private String updatedAt;
}
