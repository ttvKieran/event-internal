package com.example.iam_service.domain.repository;

import com.example.iam_service.domain.model.Employee;
import java.util.Optional;

public interface IEmployeeRepository {
    Optional<Employee> findByEmployeeCode(String employeeCode);
    Optional<Employee> findById(String id);
    Optional<Employee> findByRefreshToken(String refreshToken);
    Employee save(Employee employee);
}
