package com.example.iam_service.application.service;

import com.example.iam_service.application.in.EmployeeUseCase;
import com.example.iam_service.domain.model.Department;
import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.domain.model.Role;
import com.example.iam_service.domain.model.OutboxEvent;
import com.example.iam_service.domain.repository.IEmployeeRepository;
import com.example.iam_service.domain.repository.IOutboxEventRepository;
import com.example.iam_service.presentation.dto.CreateEmployeeRequest;
import com.example.iam_service.presentation.dto.UpdateEmployeeRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmployeeService implements EmployeeUseCase {

    private final IEmployeeRepository employeeRepository;
    private final IOutboxEventRepository outboxEventRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public EmployeeService(IEmployeeRepository employeeRepository, 
                           IOutboxEventRepository outboxEventRepository, 
                           PasswordEncoder passwordEncoder, 
                           ObjectMapper objectMapper) {
        this.employeeRepository = employeeRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Employee createEmployee(CreateEmployeeRequest request) {
        Optional<Employee> existing = employeeRepository.findByEmployeeCode(request.getEmployeeCode());
        if (existing.isPresent()) {
            throw new RuntimeException("Employee code already exists");
        }

        Optional<Employee> existingEmail = employeeRepository.findByEmail(request.getEmail());
        if (existingEmail.isPresent()) {
            throw new RuntimeException("Email already exists");
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

        Employee savedEmployee = employeeRepository.save(employee);
        
        saveOutboxEvent(savedEmployee.getId(), "EmployeeCreated", savedEmployee);
        
        return savedEmployee;
    }

    @Override
    @Transactional
    public void updateEmployee(String employeeCode, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (request.getFullname() != null && !request.getFullname().trim().isEmpty()) {
            employee.setFullname(request.getFullname());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!employee.getEmail().equals(request.getEmail())) {
                Optional<Employee> emailOwner = employeeRepository.findByEmail(request.getEmail());
                if (emailOwner.isPresent()) {
                    throw new RuntimeException("Email already exist");
                }
                employee.setEmail(request.getEmail());
            }
        }

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Employee savedEmployee = employeeRepository.save(employee);
        saveOutboxEvent(savedEmployee.getId(), "EmployeeUpdated", savedEmployee);
    }

    @Override
    @Transactional
    public void lockEmployee(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!employee.isActive()) {
            return;
        }

        employee.lockEmployee();
        Employee savedEmployee = employeeRepository.save(employee);
        saveOutboxEvent(savedEmployee.getId(), "EmployeeLocked", savedEmployee);
    }

    @Override
    @Transactional
    public void unlockEmployee(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.isActive()) {
            return;
        }

        employee.unlockEmployee();
        Employee savedEmployee = employeeRepository.save(employee);
        saveOutboxEvent(savedEmployee.getId(), "EmployeeUnlocked", savedEmployee);
    }

    private void saveOutboxEvent(String aggregateId, String type, Object payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("Employee")
                    .aggregateId(aggregateId)
                    .type(type)
                    .payload(payloadJson)
                    .createdAt(LocalDateTime.now())
                    .build();
            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize outbox event payload", e);
        }
    }
}
