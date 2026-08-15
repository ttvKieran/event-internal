package com.example.iam_service.infrastructure.persistence.adapter;

import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.domain.repository.IEmployeeRepository;
import com.example.iam_service.infrastructure.persistence.mapper.EmployeeMapper;
import com.example.iam_service.infrastructure.persistence.repository.EmployeeSpringDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EmployeeRepositoryImpl implements IEmployeeRepository {
    private final EmployeeSpringDataRepository jpaRepository;
    private final EmployeeMapper mapper;

    public EmployeeRepositoryImpl(EmployeeSpringDataRepository jpaRepository, EmployeeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Employee> findByEmployeeCode(String employeeCode) {
        return jpaRepository.findByEmployeeCode(employeeCode).map(mapper::toDomain);
    }

    @Override
    public Optional<Employee> findById(String id) {
        return jpaRepository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    public Optional<Employee> findByRefreshToken(String refreshToken) {
        return jpaRepository.findByRefreshToken(refreshToken).map(mapper::toDomain);
    }

    @Override
    public Employee save(Employee employee) {
        return null;
    }
}
