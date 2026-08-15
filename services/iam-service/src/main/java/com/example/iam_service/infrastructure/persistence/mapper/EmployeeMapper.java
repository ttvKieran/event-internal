package com.example.iam_service.infrastructure.persistence.mapper;

import com.example.iam_service.domain.model.Department;
import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.domain.model.Role;
import com.example.iam_service.infrastructure.persistence.entity.EmployeeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public Employee toDomain(EmployeeJpaEntity entity) {
        if (entity == null) return null;

        Role roleDomain = null;
        if (entity.getRole() != null) {
            roleDomain = Role.builder()
                .id(entity.getRole().getId().toString())
                .name(entity.getRole().getName())
                .build();
        }

        Department departmentDomain = null;
        if (entity.getDepartment() != null) {
            departmentDomain = Department.builder()
                .id(entity.getDepartment().getId().toString())
                .name(entity.getDepartment().getName())
                .build();
        }

        return Employee.builder()
                .id(entity.getId().toString())
                .fullname(entity.getFullname())
                .email(entity.getEmail())
                .employeeCode(entity.getEmployeeCode())
                .password(entity.getPassword())
                .status(entity.getStatus())
                .refreshToken(entity.getRefreshToken())
                .role(roleDomain)
                .department(departmentDomain)
                .build();
    }
}
