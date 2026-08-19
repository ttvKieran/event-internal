package com.example.notification_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationType {
    public static final NotificationType TICKET_CONFIRMED = new NotificationType("TICKET_CONFIRMED");
    public static final NotificationType TICKET_CANCELLED = new NotificationType("TICKET_CANCELLED");
    public static final NotificationType EVENT_CANCELLED  = new NotificationType("EVENT_CANCELLED");

    private final String code;

    public static NotificationType of(String code) {
        if (code == null) throw new IllegalArgumentException("Loại thông báo không được trống");
        return switch (code.toUpperCase()) {
            case "TICKET_CONFIRMED" -> TICKET_CONFIRMED;
            case "TICKET_CANCELLED" -> TICKET_CANCELLED;
            case "EVENT_CANCELLED"  -> EVENT_CANCELLED;
            default -> throw new IllegalArgumentException("Loại thông báo không hợp lệ: " + code);
        };
    }
}
