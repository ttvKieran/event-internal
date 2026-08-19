package com.example.iam_service.application.service;

import com.example.iam_service.application.in.EmployeeUseCase;
import com.example.iam_service.application.dto.message.EmployeeEventPayload;
import com.example.iam_service.application.port.out.IamMessagePort;
import com.example.iam_service.domain.model.Department;
import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.domain.model.Role;
import com.example.iam_service.domain.repository.IEmployeeRepository;
import com.example.iam_service.presentation.dto.CreateEmployeeRequest;
import com.example.iam_service.presentation.dto.UpdateEmployeeRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService implements EmployeeUseCase {

    private final IEmployeeRepository employeeRepository;
    private final IamMessagePort iamMessagePort;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(IEmployeeRepository employeeRepository, 
                           IamMessagePort iamMessagePort, 
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.iamMessagePort = iamMessagePort;
        this.passwordEncoder = passwordEncoder;
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
        
        iamMessagePort.publishEmployeeEvent("EmployeeCreated", toPayload(savedEmployee));
        
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
        iamMessagePort.publishEmployeeEvent("EmployeeUpdated", toPayload(savedEmployee));
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
        iamMessagePort.publishEmployeeEvent("EmployeeLocked", toPayload(savedEmployee));
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
        iamMessagePort.publishEmployeeEvent("EmployeeUnlocked", toPayload(savedEmployee));
    }

    private EmployeeEventPayload toPayload(Employee employee) {
        return EmployeeEventPayload.builder()
                .employeeId(employee.getId())
                .fullname(employee.getFullname())
                .email(employee.getEmail())
                .employeeCode(employee.getEmployeeCode())
                .status(employee.getStatus())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .roleId(employee.getRole() != null ? employee.getRole().getId() : null)
                .build();
    }
}
