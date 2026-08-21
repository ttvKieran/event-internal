package com.example.registration_service.presentation.rest;

import com.example.registration_service.application.dto.ReserveTicketDTO;
import com.example.registration_service.application.port.in.RegistrationUseCase;
import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.domain.model.valueobject.RegistrationStatus;
import com.example.registration_service.presentation.dto.request.CancelRegistrationRequest;
import com.example.registration_service.presentation.dto.request.ReserveTicketRequest;
import com.example.registration_service.presentation.dto.response.ApiResponse;
import com.example.registration_service.presentation.dto.response.CampaignStatsResponse;
import com.example.registration_service.presentation.dto.response.RegistrationResponse;
import com.example.registration_service.presentation.dto.response.ReserveTicketResponse;
import com.example.registration_service.presentation.mapper.RegistrationApiMapper;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationUseCase registrationUseCase; // Gọi đúng UseCase của Registration
    private final RegistrationApiMapper mapper;

//    @PostMapping("")
//    @Bulkhead(name = "reserveTicketBulkhead")
//    public ResponseEntity<ApiResponse<ReserveTicketResponse>> reserveTicket(
//        @RequestHeader("X-Employee-Id") UUID userId,
//        @Valid @RequestBody ReserveTicketRequest request) {
//
//        UUID registrationId = registrationUseCase.reserveTicket(mapper.toAppCommand(request, userId));
//
//        ReserveTicketResponse responseDto = new ReserveTicketResponse(
//            registrationId,
//            "Đã khóa vé, chờ thanh toán."
//        );
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//            .body(ApiResponse.ok(responseDto, "Giữ chỗ thành công. Vui lòng thanh toán (Nếu có)!"));
//    }
    @PostMapping("")
    @Bulkhead(name = "reserveTicketBulkhead")
    public ResponseEntity<ApiResponse<String>> reserveTicketAsync(
        @RequestHeader("X-Employee-Id") UUID userId,
        @Valid @RequestBody ReserveTicketRequest request) {
            ReserveTicketDTO dto = mapper.toAppCommand(request, userId);

            registrationUseCase.reserveTicketAsync(dto);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.ok(null, "Yêu cầu của bạn đang được xếp hàng xử lý..."));
        }

    @GetMapping("/status")
        public ResponseEntity<ApiResponse<String>> checkStatus(
            @RequestParam UUID campaignId,
            @RequestParam UUID userId) {
            String status = registrationUseCase.getReservationStatus(campaignId, userId);
            return ResponseEntity.ok(ApiResponse.ok(status, "Thành công"));
        }

    // Lấy danh sách
    @GetMapping
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> listRegistrations(
        @RequestParam(required = false) UUID eventId,
        @RequestParam(required = false) UUID employeeId,
        @RequestParam(required = false) String status) {

        RegistrationStatus filterStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            filterStatus = RegistrationStatus.of(status.toUpperCase());
        }
        List<Registration> list = registrationUseCase.getRegistrations(eventId, employeeId, filterStatus);

        List<RegistrationResponse> responses = list.stream()
            .map(reg -> new RegistrationResponse(
                reg.getRegistrationId(),
                reg.getCampaignId(),
                reg.getUserId(),
                reg.getStatus().getCode(),
                reg.getCancelReason(),
                reg.getRegisteredAt()
            ))
            .toList();
        return ResponseEntity.ok(ApiResponse.ok(responses, "Lấy danh sách thành công"));
    }

    // Lấy chi tiết
    @GetMapping("/{registrationId}")
    public ResponseEntity<ApiResponse<RegistrationResponse>> getRegistration(
        @PathVariable UUID registrationId) {

        Registration reg = registrationUseCase.getRegistrationById(registrationId);

        RegistrationResponse response = new RegistrationResponse(
            reg.getRegistrationId(),
            reg.getCampaignId(),
            reg.getUserId(),
            reg.getStatus().getCode(),
            reg.getCancelReason(),
            reg.getRegisteredAt()
        );
        return ResponseEntity.ok(ApiResponse.ok(response, "Lấy chi tiết thành công"));
    }

    // Hủy đăng ký sự kiện
    @PostMapping("/{registrationId}/cancel")
    public ResponseEntity<ApiResponse<RegistrationResponse>> cancelRegistration(
        @PathVariable UUID registrationId,
        @Valid @RequestBody CancelRegistrationRequest request) {

        registrationUseCase.cancelRegistration(registrationId, request.getReason());

        Registration reg = registrationUseCase.getRegistrationById(registrationId);

        RegistrationResponse response = new RegistrationResponse(
            reg.getRegistrationId(),
            reg.getCampaignId(),
            reg.getUserId(),
            reg.getStatus().getCode(),
            reg.getCancelReason(),
            reg.getRegisteredAt()
        );
        return ResponseEntity.ok(ApiResponse.ok(response, "Đăng ký đã được hủy thành công và hoàn trả slot vé."));
    }

    @GetMapping("/campaigns/{campaignId}/stats")
    public ResponseEntity<CampaignStatsResponse> getCampaignStats(@PathVariable UUID campaignId) {
        int activeTickets = registrationUseCase.countActiveRegistrations(campaignId);
        return ResponseEntity.ok(new CampaignStatsResponse(activeTickets));
    }
}
