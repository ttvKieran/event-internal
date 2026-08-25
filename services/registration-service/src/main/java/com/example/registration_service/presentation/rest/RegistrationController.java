package com.example.registration_service.presentation.rest;

import com.example.registration_service.application.model.command.CancelRegistrationCommand;
import com.example.registration_service.application.model.command.ReserveTicketCommand;
import com.example.registration_service.application.model.query.RegistrationFilter;
import com.example.registration_service.application.model.result.RegistrationResult;
import com.example.registration_service.application.port.in.RegistrationCommandUseCase;
import com.example.registration_service.application.port.in.RegistrationQueryUseCase;
import com.example.registration_service.presentation.dto.request.CancelRegistrationRequest;
import com.example.registration_service.presentation.dto.request.ReserveTicketRequest;
import com.example.registration_service.presentation.dto.response.ApiResponse;
import com.example.registration_service.presentation.dto.response.CampaignStatsResponse;
import com.example.registration_service.presentation.dto.response.RegistrationResponse;
import com.example.registration_service.presentation.mapper.RegistrationApiMapper;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationCommandUseCase registrationCommands;
    private final RegistrationQueryUseCase registrationQueries;
    private final RegistrationApiMapper mapper;

    @PostMapping
    @Bulkhead(name = "reserveTicketBulkhead")
    public ResponseEntity<ApiResponse<String>> reserveTicketAsync(
        @RequestHeader("X-Employee-Id") UUID userId,
        @Valid @RequestBody ReserveTicketRequest request) {
        ReserveTicketCommand command = mapper.toAppCommand(
            request, userId
        );

        registrationCommands.reserveTicketAsync(command);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(ApiResponse.ok(
                null,
                "Yêu cầu của bạn đang được xếp hàng xử lý..."
            ));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> checkStatus(
        @RequestParam UUID campaignId,
        @RequestParam UUID userId) {
        String status = registrationQueries.getReservationStatus(
            campaignId,
            userId
        );
        return ResponseEntity.ok(ApiResponse.ok(status, "Thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>>
        listRegistrations(
            @RequestParam(required = false) UUID eventId,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) String status) {
        RegistrationFilter filter = new RegistrationFilter(eventId, employeeId, status);

        List<RegistrationResponse> responses = registrationQueries
            .getRegistrations(filter)
            .stream()
            .map(mapper::toResponse)
            .toList();

        return ResponseEntity.ok(ApiResponse.ok(
            responses,
            "Lấy danh sách thành công"
        ));
    }

    @GetMapping("/{registrationId}")
    public ResponseEntity<ApiResponse<RegistrationResponse>>
        getRegistration(@PathVariable UUID registrationId) {
        RegistrationResult result = registrationQueries.getRegistrationById(registrationId);

        return ResponseEntity.ok(ApiResponse.ok(
            mapper.toResponse(result),
            "Lấy chi tiết thành công"
        ));
    }

    @PostMapping("/{registrationId}/cancel")
    public ResponseEntity<ApiResponse<RegistrationResponse>>
        cancelRegistration(
            @PathVariable UUID registrationId,
            @Valid @RequestBody CancelRegistrationRequest request) {
        RegistrationResult result = registrationCommands
            .cancelRegistration(new CancelRegistrationCommand(
                registrationId, request.getReason()
            ));

        return ResponseEntity.ok(ApiResponse.ok(
            mapper.toResponse(result),
            "Đăng ký đã được hủy thành công và hoàn trả slot vé."
        ));
    }

    @GetMapping("/campaigns/{campaignId}/stats")
    public ResponseEntity<CampaignStatsResponse> getCampaignStats(
        @PathVariable UUID campaignId) {
        int activeTickets = registrationQueries.countActiveRegistrations(campaignId);
        return ResponseEntity.ok(new CampaignStatsResponse(activeTickets));
    }
}
