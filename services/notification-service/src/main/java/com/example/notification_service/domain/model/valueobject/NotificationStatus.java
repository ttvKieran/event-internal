package com.example.notification_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationStatus {
    public static final NotificationStatus PENDING = new NotificationStatus("PENDING");
    public static final NotificationStatus SENT    = new NotificationStatus("SENT");
    public static final NotificationStatus FAILED  = new NotificationStatus("FAILED");

    private final String code;

    public static NotificationStatus of(String code) {
        if (code == null) throw new IllegalArgumentException("Trạng thái không được trống");
        return switch (code.toUpperCase()) {
            case "PENDING" -> PENDING;
            case "SENT"    -> SENT;
            case "FAILED"  -> FAILED;
            default -> throw new IllegalArgumentException("Trạng thái không hợp lệ: " + code);
        };
    }
}
