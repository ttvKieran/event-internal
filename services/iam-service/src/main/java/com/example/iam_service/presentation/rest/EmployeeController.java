package com.example.iam_service.presentation.rest;

import com.example.iam_service.application.in.EmployeeUseCase;
import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.presentation.dto.CreateEmployeeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/Employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeUseCase employeeUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeRequest request) {

        try {
            Employee newEmployee = employeeUseCase.createEmployee(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", newEmployee
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
