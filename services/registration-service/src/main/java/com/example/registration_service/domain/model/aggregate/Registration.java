package com.example.registration_service.domain.model.aggregate;

import com.example.registration_service.domain.model.valueobject.RegistrationStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "registrationId")
public class Registration {
    private UUID registrationId;
    private UUID campaignId;
    private UUID userId;
    private RegistrationStatus status;
    private LocalDateTime registeredAt;
    private String cancelReason;

    private Registration() {}

    public static Registration createNew(UUID campaignId, UUID userId, boolean isFreeTicket) {
        Registration reg = new Registration();
        reg.registrationId = UUID.randomUUID();
        reg.campaignId = campaignId;
        reg.userId = userId;
        reg.registeredAt = LocalDateTime.now();
        reg.status = isFreeTicket ? RegistrationStatus.CONFIRMED : RegistrationStatus.RESERVED;
        return reg;
    }

    public void confirm() {
        if (this.status != RegistrationStatus.RESERVED) {
            throw new IllegalStateException("Chỉ được confirm vé đang ở trạng thái RESERVED");
        }
        this.status = RegistrationStatus.CONFIRMED;
    }

    public void cancel(String reason) {
        if (this.status == RegistrationStatus.CANCELLED) {
            throw new IllegalStateException("Đơn đăng ký đã bị hủy từ trước");
        }
        this.status = RegistrationStatus.CANCELLED;
        this.cancelReason = reason;
    }

    public static Registration reconstitute(
        UUID registrationId, UUID campaignId, UUID userId,
        RegistrationStatus status, LocalDateTime registeredAt, String cancelReason) {

        Registration reg = new Registration();
        reg.registrationId = registrationId;
        reg.campaignId = campaignId;
        reg.userId = userId;
        reg.status = status;
        reg.registeredAt = registeredAt;
        reg.cancelReason = cancelReason;
        return reg;
    }
}
