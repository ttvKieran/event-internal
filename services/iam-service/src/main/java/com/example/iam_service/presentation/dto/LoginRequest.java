package com.example.iam_service.presentation.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String employeeCode;
    private String password;
}
