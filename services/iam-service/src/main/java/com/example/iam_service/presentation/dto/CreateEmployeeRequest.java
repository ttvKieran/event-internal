package com.example.iam_service.presentation.dto;

import lombok.Data;

@Data
public class CreateEmployeeRequest {
    private String fullname;
    private String email;
    private String employeeCode;
    private String password;
    private String roleId;
    private String departmentId;
}
