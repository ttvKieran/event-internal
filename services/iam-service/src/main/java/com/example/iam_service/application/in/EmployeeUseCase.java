package com.example.iam_service.application.in;

import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.presentation.dto.CreateEmployeeRequest;
import com.example.iam_service.presentation.dto.UpdateEmployeeRequest;

public interface EmployeeUseCase {
    Employee createEmployee(CreateEmployeeRequest request);
    void updateEmployee(UpdateEmployeeRequest request);
    void lockEmployee(String employeeId);
    void unlockEmployee(String employeeId);
}
