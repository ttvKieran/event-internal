package com.example.iam_service.application.in;

import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.presentation.dto.CreateEmployeeRequest;

public interface EmployeeUseCase {
    Employee createEmployee(CreateEmployeeRequest request);
}
