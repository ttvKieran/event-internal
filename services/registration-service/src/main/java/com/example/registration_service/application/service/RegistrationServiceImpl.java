package com.example.registration_service.application.service;

import com.example.registration_service.application.model.command.CancelRegistrationCommand;
import com.example.registration_service.application.model.command.ReserveTicketCommand;
import com.example.registration_service.application.model.query.RegistrationFilter;
import com.example.registration_service.application.model.result.RegistrationResult;
import com.example.registration_service.application.port.in.RegistrationCommandUseCase;
import com.example.registration_service.application.port.in.RegistrationQueryUseCase;
import com.example.registration_service.application.port.out.RegistrationCampaignRepository;
import com.example.registration_service.application.port.out.RegistrationIdGenerator;
import com.example.registration_service.application.port.out.RegistrationMessagePort;
import com.example.registration_service.application.port.out.RegistrationRepository;
import com.example.registration_service.application.port.out.TicketCachePort;
import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.domain.model.aggregate.RegistrationCampaign;
import com.example.registration_service.domain.model.valueobject.PaymentProvider;
import com.example.registration_service.domain.model.valueobject.RegistrationStatus;
import com.example.registration_service.domain.model.valueobject.TicketType;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class RegistrationServiceImpl
    implements RegistrationCommandUseCase, RegistrationQueryUseCase {

    private final RegistrationCampaignRepository campaignRepo;
    private final RegistrationRepository registrationRepo;
    private final RegistrationMessagePort messagePort;
    private final TicketCachePort ticketCachePort;
    private final RegistrationIdGenerator idGenerator;
    private final Clock clock;

    public RegistrationServiceImpl(RegistrationCampaignRepository campaignRepo,
        RegistrationRepository registrationRepo, RegistrationMessagePort messagePort,
        TicketCachePort ticketCachePort, RegistrationIdGenerator idGenerator, Clock clock) {
        this.campaignRepo = Objects.requireNonNull(campaignRepo);
        this.registrationRepo = Objects.requireNonNull(registrationRepo);
        this.messagePort = Objects.requireNonNull(messagePort);
        this.ticketCachePort = Objects.requireNonNull(ticketCachePort);
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public UUID reserveTicket(ReserveTicketCommand command) {
        Objects.requireNonNull(command, "command không được null");

        RegistrationCampaign campaign = campaignRepo.findByIdForUpdate(command.campaignId())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Chiến dịch bán vé"));

        boolean isFree = campaign.getTicketType() == TicketType.FREE;
        PaymentProvider provider = isFree ? null : requirePaymentProvider(command.providerCode());

        LocalDateTime now = LocalDateTime.now(clock);
        campaign.reserveTicket(now);

        Registration registration = Registration.createNew(
            idGenerator.nextId(), command.campaignId(),
            command.userId(), isFree, now
        );

        campaignRepo.save(campaign);
        registrationRepo.save(registration);

        if (isFree) messagePort.publishRegistrationConfirmed(registration);
        else messagePort.publishRegistrationRequested(registration, campaign.getPrice(), provider.getCode());

        return registration.getRegistrationId();
    }

    @Override
    public void confirmRegistration(UUID registrationId) {
        Registration registration = registrationRepo.findById(registrationId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã đăng ký"));

        registration.confirm();
        registrationRepo.save(registration);
        messagePort.publishRegistrationConfirmed(registration);
    }

    @Override
    public RegistrationResult cancelRegistration(CancelRegistrationCommand command) {
        Registration registration = registrationRepo.findByIdForUpdate(command.registrationId())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã đăng ký"));

        registration.cancel(command.reason());

        RegistrationCampaign campaign = campaignRepo.findByIdForUpdate(registration.getCampaignId())
            .orElseThrow(() -> new IllegalStateException("Không tìm thấy chiến dịch của đơn đăng ký"));

        campaign.releaseTicket();
        registrationRepo.save(registration);
        campaignRepo.save(campaign);

        ticketCachePort.incrementTicket(registration.getCampaignId());

        return toResult(registration);
    }

    @Override
    public RegistrationResult getRegistrationById(UUID registrationId) {
        return registrationRepo.findById(registrationId).map(this::toResult)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đăng ký này"));
    }

    @Override
    public List<RegistrationResult> getRegistrations(RegistrationFilter filter) {
        Objects.requireNonNull(filter, "filter không được null");

        RegistrationStatus status = filter.statusCode() == null ? null
            : RegistrationStatus.of(filter.statusCode());

        return registrationRepo.findByFilters(filter.campaignId(), filter.userId(), status)
            .stream()
            .map(this::toResult)
            .toList();
    }

    @Override
    public int countActiveRegistrations(UUID campaignId) {
        return registrationRepo.countActiveRegistrations(campaignId);
    }

    @Override
    public void reserveTicketAsync(ReserveTicketCommand command) {
        RegistrationCampaign campaign = campaignRepo.findById(command.campaignId())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện"));

        campaign.reserveTicket(LocalDateTime.now(clock));

        boolean isAvailable = ticketCachePort.decrementTicket(command.campaignId());
        if (!isAvailable) throw new IllegalStateException("Rất tiếc, sự kiện đã bán hết vé!");

        ticketCachePort.setReservationStatus(command.campaignId(), command.userId(), "PENDING");
        messagePort.publishAsyncReserveTicketCommand(command);
    }

    @Override
    public String getReservationStatus(UUID campaignId, UUID userId) {
        String status = ticketCachePort.getReservationStatus(campaignId, userId);
        return status == null ? "NOT_FOUND" : status;
    }

    @Override
    public void processAsyncReservation(ReserveTicketCommand command) {
        try {
            UUID registrationId = reserveTicket(command);
            ticketCachePort.setReservationStatus(command.campaignId(),
                command.userId(), "SUCCESS:" + registrationId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            ticketCachePort.incrementTicket(command.campaignId());
            ticketCachePort.setReservationStatus(command.campaignId(),
                command.userId(), "FAILED:" + e.getMessage());
        }
    }

    private RegistrationResult toResult(Registration registration) {
        return new RegistrationResult(
            registration.getRegistrationId(),
            registration.getCampaignId(),
            registration.getUserId(),
            registration.getStatus().getCode(),
            registration.getCancelReason(),
            registration.getRegisteredAt()
        );
    }

    private PaymentProvider requirePaymentProvider(String providerCode) {
        PaymentProvider provider = PaymentProvider.of(providerCode);
        if (provider == null) {
            throw new IllegalArgumentException("Vé thu phí bắt buộc phải chọn cổng thanh toán");
        }
        return provider;
    }
}
