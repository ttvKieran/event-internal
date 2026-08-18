package com.example.registration_service.application.service;

import com.example.registration_service.application.dto.ReserveTicketDTO;
import com.example.registration_service.application.port.in.RegistrationUseCase;
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

    @Override
    @Transactional
    public UUID reserveTicket(ReserveTicketDTO command) {
        RegistrationCampaign campaign = campaignRepo.findById(command.getCampaignId())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Chiến dịch bán vé"));

        // Có còn chỗ không? Có đúng giờ không?
        campaign.reserveTicket(LocalDateTime.now());

        boolean isFree = campaign.getTicketType() == TicketType.FREE;
        Registration registration = Registration.createNew(command.getCampaignId(), command.getUserId(), isFree);

        // Gọi đồng bộ payment service để lấy mã quét thanh toán

        campaignRepo.save(campaign);
        registrationRepo.save(registration);

        return registration.getRegistrationId();
    }

    @Override
    @Transactional
    public void confirmRegistration(UUID registrationId) {
        Registration registration = registrationRepo.findById(registrationId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã đăng ký"));

        registration.confirm();
        registrationRepo.save(registration);
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
}
