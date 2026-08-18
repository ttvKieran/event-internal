package com.example.event_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventStatus {
    public static final EventStatus DRAFT = new EventStatus("DRAFT");
    public static final EventStatus CONFIGURED = new EventStatus("CONFIGURED");
    public static final EventStatus PUBLISHED = new EventStatus("PUBLISHED");
    public static final EventStatus PENDING_CANCELLATION = new EventStatus("PENDING_CANCELLATION");
    public static final EventStatus CANCELLED = new EventStatus("CANCELLED");
    public static final EventStatus STARTED = new EventStatus("STARTED"); // Đã bổ sung
    public static final EventStatus ENDED = new EventStatus("ENDED");     // Đã bổ sung

    private final String code;

    public static EventStatus of(String code) {
        if (code == null) throw new IllegalArgumentException("Trạng thái không được trống");
        return switch (code.toUpperCase()) {
            case "DRAFT" -> DRAFT;
            case "CONFIGURED" -> CONFIGURED;
            case "PUBLISHED" -> PUBLISHED;
            case "PENDING_CANCELLATION" -> PENDING_CANCELLATION;
            case "CANCELLED" -> CANCELLED;
            case "STARTED" -> STARTED;
            case "ENDED" -> ENDED;
            default -> throw new IllegalArgumentException("Trạng thái không hợp lệ: " + code);
        };
    }

    // State Machine
    public boolean canTransitionTo(EventStatus nextStatus) {
        if (this == DRAFT && nextStatus == CONFIGURED) return true; // Nháp -> Đã cấu hình
        if (this == CONFIGURED && nextStatus == CONFIGURED) return true; // Sửa cấu hình
        if (this == CONFIGURED && nextStatus == PUBLISHED) return true; // Cấu hình -> Công bố
        if (this == PUBLISHED && nextStatus == STARTED) return true; // Công bố -> Bắt đầu diễn ra
        if (this == STARTED && nextStatus == ENDED) return true; // Đang diễn ra -> Kết thúc

        // Hủy sự kiện (Chỉ được hủy ngay khi chưa bắt đầu)
        if (nextStatus == CANCELLED && (this == DRAFT || this == CONFIGURED || this == PUBLISHED)) return true;

        // Trạng thái trung gian chờ Hủy (khi có vé đã bán)
        if (nextStatus == PENDING_CANCELLATION && this == PUBLISHED) return true;
        if (this == PENDING_CANCELLATION && nextStatus == CANCELLED) return true; // Hoàn tiền xong thì chuyển sang Hủy

        // Test
        if (this == PUBLISHED && nextStatus == PUBLISHED) return true;

        return false;
    }
}
