package com.example.resource_service.presentation.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    @Builder.Default
    private String timestamp = Instant.now().toString();
}
