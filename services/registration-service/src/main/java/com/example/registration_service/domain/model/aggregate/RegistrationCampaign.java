package com.example.registration_service.domain.model.aggregate;

import com.example.registration_service.domain.model.valueobject.CampaignStatus;
import com.example.registration_service.domain.model.valueobject.RegistrationTimeWindow;
import com.example.registration_service.domain.model.valueobject.TicketType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "campaignId")
public class RegistrationCampaign {
    private UUID campaignId;
    private TicketType ticketType;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private RegistrationTimeWindow timeWindow;
    private CampaignStatus status;

    private RegistrationCampaign() {}

    public static RegistrationCampaign createSnapshot(
        UUID campaignId, TicketType ticketType, Integer maxParticipants,
        RegistrationTimeWindow timeWindow) {

        RegistrationCampaign campaign = new RegistrationCampaign();
        campaign.campaignId = campaignId;
        campaign.ticketType = ticketType;
        campaign.maxParticipants = maxParticipants;
        campaign.currentParticipants = 0;
        campaign.timeWindow = timeWindow;
        campaign.status = CampaignStatus.PENDING;
        return campaign;
    }

    public void reserveTicket(LocalDateTime now) {
        // Event đã Publish chưa?
        if (this.status != CampaignStatus.ACTIVE) {
            throw new IllegalStateException("Chiến dịch đăng ký chưa được kích hoạt hoặc đã bị hủy.");
        }
        // Đã tới giờ chưa?
        if (now.isBefore(this.timeWindow.getOpenAt())) {
            throw new IllegalStateException("Chưa đến giờ mở bán. Vui lòng quay lại sau!");
        }
        // Đã quá hạn chưa?
        if (now.isAfter(this.timeWindow.getCloseAt())) {
            throw new IllegalStateException("Đã đóng cổng đăng ký vé!");
        }
        // Đã hết vé chưa?
        if (this.currentParticipants >= this.maxParticipants) {
            throw new IllegalStateException("Đã bán hết số lượng vé tối đa!");
        }
        this.currentParticipants++;
    }

    public void releaseTicket() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    public void changeStatus(CampaignStatus newStatus) {
        this.status = newStatus;
    }

    // Mở sớm thủ công
    public void openManually(LocalDateTime now) {
        this.status = CampaignStatus.ACTIVE;
        // Nếu hiện tại đang là sớm hơn giờ mở bán dự kiến -> Ghi đè giờ mở bán thành ngay bây giờ
        if (now.isBefore(this.timeWindow.getOpenAt())) {
            this.timeWindow = RegistrationTimeWindow.of(now, this.timeWindow.getCloseAt());
        }
    }

    // Đóng khẩn cấp
    public void closeManually(LocalDateTime now) {
        this.status = CampaignStatus.CLOSED;
        this.timeWindow = RegistrationTimeWindow.of(this.timeWindow.getOpenAt(), now);
    }

    public static RegistrationCampaign reconstitute(
        UUID campaignId, TicketType ticketType, Integer maxParticipants,
        Integer currentParticipants, RegistrationTimeWindow timeWindow, CampaignStatus status) {

        RegistrationCampaign campaign = new RegistrationCampaign();
        campaign.campaignId = campaignId;
        campaign.ticketType = ticketType;
        campaign.maxParticipants = maxParticipants;
        campaign.currentParticipants = currentParticipants;
        campaign.timeWindow = timeWindow;
        campaign.status = status;
        return campaign;
    }
}
