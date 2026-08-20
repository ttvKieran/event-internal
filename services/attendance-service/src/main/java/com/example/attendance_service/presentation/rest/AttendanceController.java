package com.example.attendance_service.presentation.rest;
import com.example.attendance_service.application.port.in.ManageQRSessionUseCase;
import com.example.attendance_service.application.port.in.ScanQrUseCase;
import com.example.attendance_service.domain.model.aggregate.QRSession;
import com.example.attendance_service.presentation.dto.request.ScanQrRequest;
import com.example.attendance_service.presentation.dto.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/v1/attendance")
public class AttendanceController {
    private final ManageQRSessionUseCase manageQRSessionUseCase;
    private final ScanQrUseCase scanQrUseCase;

    public AttendanceController(ManageQRSessionUseCase manageQRSessionUseCase, ScanQrUseCase scanQrUseCase) {
        this.manageQRSessionUseCase = manageQRSessionUseCase;
        this.scanQrUseCase = scanQrUseCase;
    }

    @PostMapping("/events/{eventId}/session")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<ApiResponse> createSession(@PathVariable String eventId) {
        QRSession session = manageQRSessionUseCase.createNewSession(eventId);
        return ResponseEntity.ok(new ApiResponse(
            true, "SUCCESS", "Create QR session successfully.",
            Map.of("sessionId", session.getId(), "qrCodes", session.getQrCodes(), "expiresAt", session.getExpiresAt()),
            Instant.now().toString()
        ));
    }

    @GetMapping("/events/{eventId}/session")
    public ResponseEntity<ApiResponse> getSession(@PathVariable String eventId) {
        java.util.Optional<QRSession> sessionOpt = manageQRSessionUseCase.getActiveSession(eventId);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse(
                false, "NOT_FOUND", "Can not found any active QR session.",
                null, Instant.now().toString()
            ));
        }
        QRSession session = sessionOpt.get();
        return ResponseEntity.ok(new ApiResponse(
            true, "SUCCESS", "Get QR session successfully.",
            Map.of("sessionId", session.getId(), "qrCodes", session.getQrCodes(), "expiresAt", session.getExpiresAt()),
            Instant.now().toString()
        ));
    }

    @PostMapping("/check-in/scan")
    public ResponseEntity<ApiResponse> scanQr(@RequestBody ScanQrRequest request) {
        scanQrUseCase.scanCheckIn(request.getEventId(), request.getEmployeeId(), request.getQrToken());
        return ResponseEntity.ok(new ApiResponse(
            true, "SUCCESS", "Scan QR successfully.",
            Map.of("status", "CHECKED_IN", "employeeId", request.getEmployeeId(), "eventId", request.getEventId(), "timestamp", Instant.now().toString()),
            Instant.now().toString()
        ));
    }
}
