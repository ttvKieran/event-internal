package com.example.resource_service.presentation.rest;

import com.example.resource_service.domain.exception.DomainException;
import com.example.resource_service.presentation.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Object>> handleDomainException(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
            .success(false).code("BAD_REQUEST").message(ex.getMessage()).build());
    }
}
