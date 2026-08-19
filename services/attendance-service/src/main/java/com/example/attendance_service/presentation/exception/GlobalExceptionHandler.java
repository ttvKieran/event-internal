package com.example.attendance_service.presentation.exception;
import com.example.attendance_service.presentation.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new ApiResponse(
            false, ApiErrorCode.BAD_REQUEST.name(), e.getMessage(), null, Instant.now().toString()
        ));
    }
}
