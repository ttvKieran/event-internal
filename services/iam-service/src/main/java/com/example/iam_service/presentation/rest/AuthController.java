package com.example.iam_service.presentation.rest;

import com.example.iam_service.application.dto.TokenPairDto;
import com.example.iam_service.application.in.AuthUseCase;
import com.example.iam_service.domain.model.Employee;
import com.example.iam_service.presentation.dto.LoginRequest;
import com.example.iam_service.presentation.dto.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        TokenPairDto pair = authUseCase.authenticate(request.getEmployeeCode(), request.getPassword());
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", pair
        ));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenPairDto pair = authUseCase.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", pair
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("X-Employee-Id") String employeeId) {
        Employee emp = authUseCase.getProfile(employeeId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", emp
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("X-Employee-Id") String employeeId) {
        authUseCase.logout(employeeId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Log out successfully"));
    }
}
