package com.example.registration_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CampaignStatus {
    // Chờ Event Service gửi lệnh Publish
    public static final CampaignStatus PENDING = new CampaignStatus("PENDING");
    // Đã Publish
    public static final CampaignStatus ACTIVE = new CampaignStatus("ACTIVE");
    public static final CampaignStatus CLOSED = new CampaignStatus("CLOSED");
    // Sự kiện bị hủy
    public static final CampaignStatus CANCELLED = new CampaignStatus("CANCELLED");

    private final String code;

    public static CampaignStatus of(String code) {
        if (code == null) throw new IllegalArgumentException("Trạng thái chiến dịch không được trống");
        return switch (code.toUpperCase()) {
            case "PENDING" -> PENDING;
            case "ACTIVE" -> ACTIVE;
            case "CLOSED" -> CLOSED; // BỔ SUNG DÒNG NÀY
            case "CANCELLED" -> CANCELLED;
            default -> throw new IllegalArgumentException("Trạng thái không hợp lệ: " + code);
        };
    }
}
