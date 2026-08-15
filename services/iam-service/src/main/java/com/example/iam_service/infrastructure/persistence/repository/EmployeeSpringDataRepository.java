package com.example.iam_service.infrastructure.persistence.repository;

import com.example.iam_service.infrastructure.persistence.entity.EmployeeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeSpringDataRepository extends JpaRepository<EmployeeJpaEntity, UUID> {
    Optional<EmployeeJpaEntity> findByEmployeeCode(String employeeCode);
    Optional<EmployeeJpaEntity> findByRefreshToken(String refreshToken);
}
