package com.example.event_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import java.time.LocalDateTime;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventSchedule {
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime registrationOpenAt;
    LocalDateTime registrationCloseAt;

    public static EventSchedule of(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime openAt, LocalDateTime closeAt) {
        if (startTime == null || endTime == null) throw new IllegalArgumentException("Phải có giờ bắt đầu và kết thúc");
        if (endTime.isBefore(startTime)) throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
        if (openAt != null && closeAt != null && closeAt.isBefore(openAt))
            throw new IllegalArgumentException("Giờ đóng đăng ký phải sau giờ mở");

        return new EventSchedule(startTime, endTime, openAt, closeAt);
    }
}


