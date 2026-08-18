package com.example.registration_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationStatus {
    public static final RegistrationStatus RESERVED = new RegistrationStatus("RESERVED");
    public static final RegistrationStatus CONFIRMED = new RegistrationStatus("CONFIRMED");
    public static final RegistrationStatus CANCELLED = new RegistrationStatus("CANCELLED");

    private final String code;

    public static RegistrationStatus of(String code) {
        if (code == null) throw new IllegalArgumentException("Trạng thái đăng ký không được trống");
        return switch (code.toUpperCase()) {
            case "RESERVED" -> RESERVED;
            case "CONFIRMED" -> CONFIRMED;
            case "CANCELLED" -> CANCELLED;
            default -> throw new IllegalArgumentException("Trạng thái không hợp lệ: " + code);
        };
    }
}
