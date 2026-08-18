package com.example.registration_service.presentation.rest;

import com.example.registration_service.application.port.in.RegistrationUseCase;
import com.example.registration_service.presentation.dto.response.ApiResponse;
import com.example.registration_service.presentation.dto.response.CampaignStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventRegistrationAdminController {

    private final RegistrationUseCase registrationUseCase;

    @PostMapping("/{eventId}/registration/open")
    public ResponseEntity<ApiResponse<CampaignStatusResponse>> openRegistration(
        @PathVariable UUID eventId) {
        registrationUseCase.openRegistration(eventId);
        CampaignStatusResponse responseDto = new CampaignStatusResponse(
            eventId,
            "ACTIVE",
            LocalDateTime.now(),
            null
        );
        return ResponseEntity.ok(ApiResponse.ok(responseDto, "Thao tác cập nhật trạng thái cổng đăng ký thành công."));
    }

    @PostMapping("/{eventId}/registration/close")
    public ResponseEntity<ApiResponse<CampaignStatusResponse>> closeRegistration(
        @PathVariable UUID eventId) {
        registrationUseCase.closeRegistration(eventId);
        CampaignStatusResponse responseDto = new CampaignStatusResponse(
            eventId,
            "CLOSED",
            null,
            LocalDateTime.now()
        );
        return ResponseEntity.ok(ApiResponse.ok(responseDto, "Thao tác cập nhật trạng thái cổng đăng ký thành công."));
    }
}
