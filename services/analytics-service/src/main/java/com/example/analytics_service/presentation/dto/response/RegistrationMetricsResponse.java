package com.example.analytics_service.presentation.dto.response;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data @Builder
public class RegistrationMetricsResponse {
    private UUID eventId;
    private int totalRegistrations;
    private int freeCount;
    private int paidCount;
    private double fillRate; 
    private String updatedAt;
}
