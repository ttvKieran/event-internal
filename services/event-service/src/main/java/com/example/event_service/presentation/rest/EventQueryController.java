package com.example.event_service.presentation.rest;

import com.example.event_service.application.dto.query.EventDetailsDTO;
import com.example.event_service.application.query.port.EventQueryPort;
import com.example.event_service.presentation.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventQueryController {

    private final EventQueryPort eventQueryPort;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDetailsDTO>>> listEvents(
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        // TODO: Thêm listEvents vào EventQueryPort, sau đó gọi và trả về
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventDetailsDTO>> getEvent(@PathVariable UUID eventId) {

        // Hàm này chúng ta đã viết code ở EventQueryPortImpl trước đó!
        EventDetailsDTO eventDetails = eventQueryPort.fetchEventDetails(eventId);

        // Nếu không tìm thấy, văng lỗi để GlobalExceptionHandler bắt và sinh ra mã HTTP 404
        if (eventDetails == null) {
            throw new IllegalArgumentException("Sự kiện không tồn tại");
            // (Tương lai bạn có thể tạo EventNotFoundException kế thừa RuntimeException để quăng ra thay thế)
        }

        return ResponseEntity.ok(ApiResponse.ok(eventDetails));
    }
}
