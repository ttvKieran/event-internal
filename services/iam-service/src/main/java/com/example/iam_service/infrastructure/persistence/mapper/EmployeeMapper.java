package com.example.iam_service.infrastructure.persistence.mapper;

import com.example.iam_service.domain.model.Department;
import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.domain.model.Role;
import com.example.iam_service.infrastructure.persistence.entity.DepartmentJpaEntity;
import com.example.iam_service.infrastructure.persistence.entity.EmployeeJpaEntity;
import com.example.iam_service.infrastructure.persistence.entity.RoleJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
                .id(entity.getId() != null ? entity.getId().toString() : null)
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

    public EmployeeJpaEntity toEntity(Employee domain) {
        if (domain == null) return null;

        EmployeeJpaEntity entity = new EmployeeJpaEntity();
        if (domain.getId() != null && !domain.getId().isEmpty()) {
            entity.setId(UUID.fromString(domain.getId()));
        }
        entity.setFullname(domain.getFullname());
        entity.setEmail(domain.getEmail());
        entity.setEmployeeCode(domain.getEmployeeCode());
        entity.setPassword(domain.getPassword());
        entity.setStatus(domain.getStatus());
        entity.setRefreshToken(domain.getRefreshToken());

        if (domain.getRole() != null && domain.getRole().getId() != null) {
            RoleJpaEntity roleEntity = new RoleJpaEntity();
            roleEntity.setId(UUID.fromString(domain.getRole().getId()));
            entity.setRole(roleEntity);
        }

        if (domain.getDepartment() != null && domain.getDepartment().getId() != null) {
            DepartmentJpaEntity deptEntity = new DepartmentJpaEntity();
            deptEntity.setId(UUID.fromString(domain.getDepartment().getId()));
            entity.setDepartment(deptEntity);
        }

        return entity;
    }
}
