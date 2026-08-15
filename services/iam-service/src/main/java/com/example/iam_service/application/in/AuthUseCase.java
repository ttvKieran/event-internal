package com.example.iam_service.application.in;

import com.example.iam_service.application.dto.TokenPairDto;
import com.example.iam_service.domain.model.Employee;

public interface AuthUseCase {
    TokenPairDto authenticate(String employeeCode, String password);
    TokenPairDto refreshToken(String refreshToken);
    Employee getProfile(String employeeId);
    void logout(String employeeId);
}
