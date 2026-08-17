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
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setFullname(domain.getFullname());
        entity.setEmail(domain.getEmail());
        entity.setEmployeeCode(domain.getEmployeeCode());
        entity.setPassword(domain.getPassword());
        entity.setStatus(domain.getStatus());
        entity.setRefreshToken(domain.getRefreshToken());

        if (domain.getRole() != null && domain.getRole().getId() != null) {
            com.example.iam_service.infrastructure.persistence.entity.RoleJpaEntity roleEntity = new com.example.iam_service.infrastructure.persistence.entity.RoleJpaEntity();
            roleEntity.setId(java.util.UUID.fromString(domain.getRole().getId()));
            entity.setRole(roleEntity);
        }

        if (domain.getDepartment() != null && domain.getDepartment().getId() != null) {
            com.example.iam_service.infrastructure.persistence.entity.DepartmentJpaEntity deptEntity = new com.example.iam_service.infrastructure.persistence.entity.DepartmentJpaEntity();
            deptEntity.setId(java.util.UUID.fromString(domain.getDepartment().getId()));
            entity.setDepartment(deptEntity);
        }

        return entity;
    }
}
