package com.example.iam_service.application.dto;

import com.example.iam_service.domain.model.Employee;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenPairDto {
    private String accessToken;
    private String refreshToken;
    private Employee employee;
}
