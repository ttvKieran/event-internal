package com.example.registration_service.application.service;

import com.example.registration_service.application.dto.ReserveTicketDTO;
import com.example.registration_service.application.port.in.RegistrationUseCase;
import com.example.registration_service.application.port.out.RegistrationMessagePort;
import com.example.registration_service.application.port.out.TicketCachePort;
import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.domain.model.aggregate.RegistrationCampaign;
import com.example.registration_service.domain.model.valueobject.CampaignStatus;
import com.example.registration_service.domain.model.valueobject.RegistrationStatus;
import com.example.registration_service.domain.model.valueobject.RegistrationTimeWindow;
import com.example.registration_service.domain.model.valueobject.TicketType;
import com.example.registration_service.domain.repository.RegistrationCampaignRepository;
import com.example.registration_service.domain.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationUseCase {

    private final RegistrationCampaignRepository campaignRepo;
    private final RegistrationRepository registrationRepo;
    private final RegistrationMessagePort messagePort;
     private final TicketCachePort ticketCachePort;

    @Override
    @Transactional
    public UUID reserveTicket(ReserveTicketDTO command) {
        RegistrationCampaign campaign = campaignRepo.findByIdForUpdate(command.getCampaignId())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Chiến dịch bán vé"));

        // Có còn chỗ không? Có đúng giờ không?
        campaign.reserveTicket(LocalDateTime.now());

        boolean isFree = campaign.getTicketType() == TicketType.FREE;
        Registration registration = Registration.createNew(command.getCampaignId(), command.getUserId(), isFree);

        campaignRepo.save(campaign);
        registrationRepo.save(registration);

        // Nếu là vé PAID → Saga, bắn Event để Payment Service tạo giao dịch
        if (!isFree) {
            if (command.getProvider() == null) {
                throw new IllegalArgumentException("Vé thu phí bắt buộc phải chọn cổng thanh toán (provider)");
            }
            messagePort.publishRegistrationRequested(registration, campaign.getPrice(), command.getProvider().getCode());
        }
        // Nếu là vé FREE → Tự confirm luôn, bắn Event thông báo
        else {
            messagePort.publishRegistrationConfirmed(registration);
        }

        return registration.getRegistrationId();
    }

    @Override
    @Transactional
    public void confirmRegistration(UUID registrationId) {
        Registration registration = registrationRepo.findById(registrationId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã đăng ký"));

        registration.confirm();
        registrationRepo.save(registration);

        messagePort.publishRegistrationConfirmed(registration);
    }

    @Override
    @Transactional
    public void cancelRegistration(UUID registrationId, String reason) {
        Registration registration = registrationRepo.findById(registrationId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã đăng ký"));

        registration.cancel(reason);

        RegistrationCampaign campaign = campaignRepo.findById(registration.getCampaignId()).get();
        campaign.releaseTicket();

        registrationRepo.save(registration);
        campaignRepo.save(campaign);
    }

    @Override
    @Transactional
    public void openRegistration(UUID campaignId) {
        RegistrationCampaign campaign = campaignRepo.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Chiến dịch bán vé"));
        campaign.openManually(LocalDateTime.now());
        campaignRepo.save(campaign);
    }

    @Override
    @Transactional
    public void closeRegistration(UUID campaignId) {
        RegistrationCampaign campaign = campaignRepo.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Chiến dịch bán vé"));
        campaign.closeManually(LocalDateTime.now());
        campaignRepo.save(campaign);
    }

    @Override
    @Transactional(readOnly = true)
    public Registration getRegistrationById(UUID registrationId) {
        return registrationRepo.findById(registrationId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đăng ký này"));
    }
    @Override
    @Transactional(readOnly = true)
    public List<Registration> getRegistrations(UUID campaignId, UUID userId, RegistrationStatus status) {
        return registrationRepo.findByFilters(campaignId, userId, status);
    }

    @Override
    public int countActiveRegistrations(UUID campaignId) {
        return registrationRepo.countActiveRegistrations(campaignId);
    }

    @Override
    @Transactional
    public void reserveTicketAsync(ReserveTicketDTO command) {
        RegistrationCampaign campaign = campaignRepo.findById(command.getCampaignId())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện"));

        campaign.reserveTicket(LocalDateTime.now());
        boolean isAvailable = ticketCachePort.decrementTicket(command.getCampaignId());
        if (!isAvailable) {
            throw new IllegalStateException("Rất tiếc, sự kiện đã bán hết vé!");
        }
        ticketCachePort.setReservationStatus(command.getCampaignId(), command.getUserId(), "PENDING");
        messagePort.publishAsyncReserveTicketCommand(command);
    }

    @Override
    public String getReservationStatus(UUID campaignId, UUID userId) {
        String status = ticketCachePort.getReservationStatus(campaignId, userId);
        if (status == null) return "NOT_FOUND";
        return status;
    }

    @Override
    public void processAsyncReservation(ReserveTicketDTO command) {
        try {
            UUID registrationId = this.reserveTicket(command);
            ticketCachePort.setReservationStatus(command.getCampaignId(), command.getUserId(), "SUCCESS:" + registrationId);
        } catch (Exception e) {
            ticketCachePort.setReservationStatus(command.getCampaignId(), command.getUserId(), "FAILED:" + e.getMessage());
        }
    }
}
