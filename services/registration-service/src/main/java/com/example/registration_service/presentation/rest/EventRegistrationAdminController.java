package com.example.registration_service.presentation.rest;

import com.example.registration_service.application.model.result.CampaignStatusResult;
import com.example.registration_service.application.port.in.RegistrationWindowUseCase;
import com.example.registration_service.presentation.dto.response.ApiResponse;
import com.example.registration_service.presentation.dto.response.CampaignStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventRegistrationAdminController {

    private final RegistrationWindowUseCase registrationWindow;

    @PostMapping("/{eventId}/registration/open")
    public ResponseEntity<ApiResponse<CampaignStatusResponse>>
        openRegistration(@PathVariable UUID eventId) {
        CampaignStatusResult result = registrationWindow
            .openRegistration(eventId);

        return ResponseEntity.ok(ApiResponse.ok(
            toResponse(result),
            "Thao tác cập nhật trạng thái cổng đăng ký thành công."
        ));
    }

    @PostMapping("/{eventId}/registration/close")
    public ResponseEntity<ApiResponse<CampaignStatusResponse>>
        closeRegistration(@PathVariable UUID eventId) {
        CampaignStatusResult result = registrationWindow
            .closeRegistration(eventId);

        return ResponseEntity.ok(ApiResponse.ok(
            toResponse(result),
            "Thao tác cập nhật trạng thái cổng đăng ký thành công."
        ));
    }

    private CampaignStatusResponse toResponse(
        CampaignStatusResult result) {
        return new CampaignStatusResponse(
            result.campaignId(),
            result.status(),
            result.openedAt(),
            result.closedAt()
        );
    }
}
