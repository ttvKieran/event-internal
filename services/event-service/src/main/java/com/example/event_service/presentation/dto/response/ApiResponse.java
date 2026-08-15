package com.example.event_service.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.event_service.presentation.exception.ApiErrorCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {

    @Builder.Default
    private boolean success = true;

    private String code;
    private String message;
    private T data;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
            .code(ApiErrorCode.SUCCESS.getCode())
            .message(ApiErrorCode.SUCCESS.getDefaultMessage())
            .data(data)
            .build();
    }

    // Helper truyền custom message
    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
            .code(ApiErrorCode.SUCCESS.getCode())
            .message(message)
            .data(data)
            .build();
    }
}
