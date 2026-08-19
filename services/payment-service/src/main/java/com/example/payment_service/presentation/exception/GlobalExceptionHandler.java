package com.example.payment_service.presentation.exception;

import com.example.payment_service.presentation.dto.response.ErrorResponseDTO;
import com.example.payment_service.presentation.exception.ApiErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad Request - Path: {}, Message: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .code(ApiErrorCode.BAD_REQUEST.getCode())
            .message(ex.getMessage() != null ? ex.getMessage() : ApiErrorCode.BAD_REQUEST.getDefaultMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    //IllegalStateException
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Business Logic Conflict - Path: {}, Message: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .code(ApiErrorCode.BAD_REQUEST.getCode())

            .message(ex.getMessage())

            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Bắt toàn bộ các Exception không lường trước được (tránh crash hoặc lộ stacktrace)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex, HttpServletRequest request) {
        log.error("Internal Server Error - Path: {}", request.getRequestURI(), ex);

        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .code(ApiErrorCode.INTERNAL_SERVER_ERROR.getCode())
            .message(ApiErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
