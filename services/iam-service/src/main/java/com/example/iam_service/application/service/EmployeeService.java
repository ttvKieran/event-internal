package com.example.iam_service.application.service;

import com.example.iam_service.application.in.EmployeeUseCase;
import com.example.iam_service.domain.model.Department;
import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.domain.model.Role;
import com.example.iam_service.domain.repository.IEmployeeRepository;
import com.example.iam_service.presentation.dto.CreateEmployeeRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService implements EmployeeUseCase {

    private final IEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(IEmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Employee createEmployee(CreateEmployeeRequest request) {
        Optional<Employee> existing = employeeRepository.findByEmployeeCode(request.getEmployeeCode());
        if (existing.isPresent()) {
            throw new RuntimeException("Employee code already exists");
        }

        Role role = Role.builder().id(request.getRoleId()).build();
        Department department = Department.builder().id(request.getDepartmentId()).build();

        Employee employee = Employee.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .employeeCode(request.getEmployeeCode())
                .password(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .role(role)
                .department(department)
                .build();

        return employeeRepository.save(employee);
    }
}
