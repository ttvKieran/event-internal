package com.example.analytics_service.presentation.rest;
import com.example.analytics_service.application.port.in.QueryMetricsUseCase;
import com.example.analytics_service.domain.model.aggregate.EventMetrics;
import com.example.analytics_service.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final QueryMetricsUseCase queryMetricsUseCase;

    @GetMapping("/events/{eventId}/dashboard")
    public ResponseEntity<DashboardMetricsResponse> getDashboardMetrics(@PathVariable UUID eventId) {
        EventMetrics metrics = queryMetricsUseCase.getMetrics(eventId);
        DashboardMetricsResponse response = DashboardMetricsResponse.builder()
                .eventId(metrics.getEventId())
                .registrationCount(metrics.getTotalRegistrations())
                .freeRegistrationCount(metrics.getFreeCount())
                .paidRegistrationCount(metrics.getPaidCount())
                .cancelledCount(metrics.getCancelledCount())
                .revenue(metrics.getRevenue())
                .checkInRate(metrics.getCheckInRate())
                .fillRate(metrics.getFillRate())
                .updatedAt(metrics.getUpdatedAt().toString())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/{eventId}/registration-metrics")
    public ResponseEntity<RegistrationMetricsResponse> getRegistrationMetrics(@PathVariable UUID eventId) {
        EventMetrics metrics = queryMetricsUseCase.getMetrics(eventId);
        RegistrationMetricsResponse response = RegistrationMetricsResponse.builder()
                .eventId(metrics.getEventId())
                .totalRegistrations(metrics.getTotalRegistrations())
                .freeCount(metrics.getFreeCount())
                .paidCount(metrics.getPaidCount())
                .fillRate(metrics.getFillRate())
                .updatedAt(metrics.getUpdatedAt().toString())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/{eventId}/attendance-metrics")
    public ResponseEntity<AttendanceMetricsResponse> getAttendanceMetrics(@PathVariable UUID eventId) {
        EventMetrics metrics = queryMetricsUseCase.getMetrics(eventId);
        AttendanceMetricsResponse response = AttendanceMetricsResponse.builder()
                .eventId(metrics.getEventId())
                .checkedInCount(metrics.getCheckedInCount())
                .checkInRate(metrics.getCheckInRate())
                .updatedAt(metrics.getUpdatedAt().toString())
                .build();
        return ResponseEntity.ok(response);
    }
}
