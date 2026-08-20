package com.example.attendance_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String code;
    private String message;
    private Map<String, Object> data;
    private String timestamp;
}
