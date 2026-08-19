package com.example.payment_service.presentation.exception;

import lombok.Getter;

@Getter
public enum ApiErrorCode {
    SUCCESS("SUCCESS", "Thao tác thành công"),
    BAD_REQUEST("BAD_REQUEST", "Dữ liệu đầu vào không hợp lệ"),
    UNAUTHORIZED("UNAUTHORIZED", "Xác thực không thành công hoặc JWT hết hạn"),
    FORBIDDEN("FORBIDDEN", "Tài khoản không đủ quyền truy cập tài nguyên này"),
    NOT_FOUND("NOT_FOUND", "Tài nguyên được yêu cầu không tồn tại"),
    CONFLICT("CONFLICT", "Xung đột trạng thái tài nguyên"),
    INTERNAL_SERVER_ERROR("INTERNAL_ERROR", "Lỗi hệ thống nội bộ");

    private final String code;
    private final String defaultMessage;

    ApiErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
