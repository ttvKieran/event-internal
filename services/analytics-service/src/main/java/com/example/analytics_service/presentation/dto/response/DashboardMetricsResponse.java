package com.example.analytics_service.presentation.dto.response;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
@Data @Builder
public class DashboardMetricsResponse {
    private UUID eventId;
    private int registrationCount;
    private int freeRegistrationCount;
    private int paidRegistrationCount;
    private int cancelledCount;
    private BigDecimal revenue;
    private double checkInRate;
    private double fillRate;
    private String updatedAt;
}
