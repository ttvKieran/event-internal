package com.example.iam_service.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEventPayload {
    private String employeeId;
    private String fullname;
    private String email;
    private String employeeCode;
    private String status;
    private String departmentId;
    private String roleId;
}
