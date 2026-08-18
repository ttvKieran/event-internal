package com.example.registration_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationTimeWindow {
    private final LocalDateTime openAt;
    private final LocalDateTime closeAt;

    public static RegistrationTimeWindow of(LocalDateTime openAt, LocalDateTime closeAt) {
        if (openAt != null && closeAt != null && openAt.isAfter(closeAt)) {
            throw new IllegalArgumentException("Thời gian mở bán không được trễ hơn thời gian đóng");
        }
        return new RegistrationTimeWindow(openAt, closeAt);
    }

    public boolean isOpen(LocalDateTime now) {
        if (openAt == null || closeAt == null) return false;
        return !now.isBefore(openAt) && !now.isAfter(closeAt);
    }
}
