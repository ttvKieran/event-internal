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

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or #id.equals(authentication.name)")
    public ResponseEntity<?> updateEmployee(
            @PathVariable("id") String id,
            @RequestBody com.example.iam_service.presentation.dto.UpdateEmployeeRequest request) {
        try {
            employeeUseCase.updateEmployee(id, request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Update information successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/lock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> lockEmployee(@PathVariable("id") String id, org.springframework.security.core.Authentication authentication) {
        if (id.equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Cannot lock your own account"
            ));
        }
        try {
            employeeUseCase.lockEmployee(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Account has been locked"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> unlockEmployee(@PathVariable("id") String id) {
        try {
            employeeUseCase.unlockEmployee(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Restore account successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
