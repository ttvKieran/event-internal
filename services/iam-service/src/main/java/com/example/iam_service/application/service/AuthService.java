package com.example.iam_service.application.service;

import com.example.iam_service.application.dto.TokenPairDto;
import com.example.iam_service.application.in.AuthUseCase;
import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.domain.repository.IEmployeeRepository;
import com.example.iam_service.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements AuthUseCase {

    private final IEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(IEmployeeRepository employeeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public TokenPairDto authenticate(String employeeCode, String password) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
            .orElseThrow(() -> new RuntimeException("Employee not found!"));

        if (!employee.isActive()) {
            throw new RuntimeException("Account is locked!");
        }

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            throw new RuntimeException("Password is incorrect");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(employee);
        String refreshToken = jwtTokenProvider.generateRefreshToken(employee);

        employee.setRefreshToken(refreshToken);
        employeeRepository.save(employee);

        return TokenPairDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .employee(employee)
            .build();
    }

    @Override
    public TokenPairDto refreshToken(String refreshToken) {
        Employee employee = employeeRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new RuntimeException("invalid refresh token"));

        String accessToken = jwtTokenProvider.generateAccessToken(employee);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(employee);

        employee.setRefreshToken(refreshToken);
        employeeRepository.save(employee);

        return TokenPairDto.builder()
            .accessToken(accessToken)
            .refreshToken(newRefreshToken)
            .employee(employee)
            .build();
    }

    @Override
    public Employee getProfile(String employeeId) {
        return employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public void logout(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Account not found!"));

        employee.setRefreshToken(null);
        employeeRepository.save(employee);
    }
}
