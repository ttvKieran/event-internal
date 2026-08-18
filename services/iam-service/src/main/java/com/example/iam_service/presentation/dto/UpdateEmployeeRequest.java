package com.example.iam_service.presentation.dto;

import lombok.Data;

@Data
public class UpdateEmployeeRequest {
    private String fullname;
    private String employeeCode;
    private String email;
    private String password;
}
