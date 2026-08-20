package com.example.notification_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationChannel {

    public static final NotificationChannel EMAIL = new NotificationChannel("EMAIL");

    private final String code;

    public static NotificationChannel of(String code) {
        if (code == null) throw new IllegalArgumentException("Kênh thông báo không được trống");
        return switch (code.toUpperCase()) {
            case "EMAIL" -> EMAIL;
            default -> throw new IllegalArgumentException("Kênh thông báo không hợp lệ: " + code);
        };
    }
}
