package com.example.event_service.presentation.rest;

import com.example.event_service.application.dto.EventDetailsDTO;
import com.example.event_service.application.port.in.EventUseCase;
import com.example.event_service.presentation.dto.request.ConfigureEventDetailsRequestDTO;
import com.example.event_service.presentation.dto.request.CreateEventRequestDTO;
import com.example.event_service.presentation.dto.response.ApiResponse;
import com.example.event_service.presentation.dto.response.EventResponseDTO;
import com.example.event_service.presentation.mapper.EventApiMapper; // Import Mapper
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventUseCase eventUseCase;

    private final EventApiMapper mapper;

    // Tạo sự kiện
    @PostMapping
    public ResponseEntity<ApiResponse<EventResponseDTO>> createEvent(@RequestBody CreateEventRequestDTO request) {
        EventDetailsDTO appResult = eventUseCase.createEvent(mapper.toAppDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(mapper.toResponseDto(appResult)));
    }

    // Lấy chi tiết sự kiện
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponseDTO>> getEvent(@PathVariable UUID eventId) {
        EventDetailsDTO appResult = eventUseCase.getEventDetails(eventId);

        EventResponseDTO response = mapper.toResponseDto(appResult);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // Cấu hình sự kiện
    @PutMapping("/{eventId}/details")
    public ResponseEntity<ApiResponse<EventResponseDTO>> configureEventDetails(
        @PathVariable UUID eventId,
        @RequestBody ConfigureEventDetailsRequestDTO request) {

        EventDetailsDTO appResult =  eventUseCase.configureEventDetails(eventId, mapper.toAppDto(request));

        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponseDto(appResult)));
    }

    // Công bố sự kiện
    @PostMapping("/{eventId}/publish")
    public ResponseEntity<ApiResponse<EventResponseDTO>> publishEvent(@PathVariable UUID eventId) {
        EventDetailsDTO appResult = eventUseCase.publishEvent(eventId);
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponseDto(appResult)));
    }

    // Hủy sự kiện
    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<ApiResponse<EventResponseDTO>> cancelEvent(
        @PathVariable UUID eventId,
        @RequestBody Map<String, String> request) {

        String reason = request.get("reason");
        EventDetailsDTO appResult = eventUseCase.cancelEvent(eventId, reason);

        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponseDto(appResult)));
    }
}
