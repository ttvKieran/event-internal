package com.example.registration_service.application.service;

import com.example.registration_service.application.model.command.ReserveTicketCommand;
import com.example.registration_service.application.model.result.RegistrationResult;
import com.example.registration_service.application.port.out.RegistrationCampaignRepository;
import com.example.registration_service.application.port.out.RegistrationIdGenerator;
import com.example.registration_service.application.port.out.RegistrationMessagePort;
import com.example.registration_service.application.port.out.RegistrationRepository;
import com.example.registration_service.application.port.out.TicketCachePort;
import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.domain.model.aggregate.RegistrationCampaign;
import com.example.registration_service.domain.model.valueobject.CampaignStatus;
import com.example.registration_service.domain.model.valueobject.RegistrationStatus;
import com.example.registration_service.domain.model.valueobject.RegistrationTimeWindow;
import com.example.registration_service.domain.model.valueobject.TicketType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    private static final UUID CAMPAIGN_ID = UUID.fromString(
        "10000000-0000-0000-0000-000000000001"
    );
    private static final UUID USER_ID = UUID.fromString(
        "20000000-0000-0000-0000-000000000002"
    );
    private static final UUID REGISTRATION_ID = UUID.fromString(
        "30000000-0000-0000-0000-000000000003"
    );
    private static final Clock CLOCK = Clock.fixed(
        Instant.parse("2026-01-15T10:00:00Z"),
        ZoneOffset.UTC
    );

    @Mock
    private RegistrationCampaignRepository campaignRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private RegistrationMessagePort messagePort;
    @Mock
    private TicketCachePort ticketCache;
    @Mock
    private RegistrationIdGenerator idGenerator;

    private RegistrationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RegistrationServiceImpl(
            campaignRepository,
            registrationRepository,
            messagePort,
            ticketCache,
            idGenerator,
            CLOCK
        );
    }

    @Test
    void paidTicketWithoutProviderFailsBeforeChangingCampaign() {
        RegistrationCampaign campaign = paidCampaign(0, 10);
        when(campaignRepository.findByIdForUpdate(CAMPAIGN_ID))
            .thenReturn(Optional.of(campaign));

        assertThrows(
            IllegalArgumentException.class,
            () -> service.reserveTicket(
                new ReserveTicketCommand(CAMPAIGN_ID, USER_ID, null)
            )
        );

        assertEquals(0, campaign.getCurrentParticipants());
        verify(campaignRepository, never()).save(any());
        verifyNoInteractions(registrationRepository, messagePort);
    }

    @Test
    void queryReturnsApplicationResultInsteadOfDomainAggregate() {
        LocalDateTime registeredAt = LocalDateTime.now(CLOCK);
        Registration registration = Registration.reconstitute(
            REGISTRATION_ID,
            CAMPAIGN_ID,
            USER_ID,
            RegistrationStatus.CONFIRMED,
            registeredAt,
            null
        );
        when(registrationRepository.findById(REGISTRATION_ID))
            .thenReturn(Optional.of(registration));

        RegistrationResult result = service.getRegistrationById(
            REGISTRATION_ID
        );

        assertEquals(REGISTRATION_ID, result.registrationId());
        assertEquals("CONFIRMED", result.status());
        assertEquals(registeredAt, result.registeredAt());
    }

    @Test
    void asyncBusinessFailureIsRecordedWithoutRetry() {
        RegistrationCampaign soldOutCampaign = paidCampaign(10, 10);
        when(campaignRepository.findByIdForUpdate(CAMPAIGN_ID))
            .thenReturn(Optional.of(soldOutCampaign));

        service.processAsyncReservation(
            new ReserveTicketCommand(CAMPAIGN_ID, USER_ID, "VNPAY")
        );

        verify(ticketCache).setReservationStatus(
            eq(CAMPAIGN_ID),
            eq(USER_ID),
            startsWith("FAILED:")
        );
        verifyNoInteractions(messagePort);
    }

    @Test
    void asyncTechnicalFailureEscapesForRollbackAndRetry() {
        RegistrationCampaign campaign = paidCampaign(0, 10);
        when(campaignRepository.findByIdForUpdate(CAMPAIGN_ID))
            .thenReturn(Optional.of(campaign));
        when(idGenerator.nextId()).thenReturn(REGISTRATION_ID);
        doThrow(new RuntimeException("database unavailable"))
            .when(registrationRepository)
            .save(any(Registration.class));

        assertThrows(
            RuntimeException.class,
            () -> service.processAsyncReservation(
                new ReserveTicketCommand(
                    CAMPAIGN_ID,
                    USER_ID,
                    "VNPAY"
                )
            )
        );

        verify(ticketCache, never()).setReservationStatus(
            eq(CAMPAIGN_ID),
            eq(USER_ID),
            startsWith("FAILED:")
        );
    }

    private RegistrationCampaign paidCampaign(
        int currentParticipants,
        int maxParticipants) {
        LocalDateTime now = LocalDateTime.now(CLOCK);
        return RegistrationCampaign.reconstitute(
            CAMPAIGN_ID,
            TicketType.PAID,
            maxParticipants,
            currentParticipants,
            BigDecimal.TEN,
            RegistrationTimeWindow.of(
                now.minusHours(1),
                now.plusHours(1)
            ),
            CampaignStatus.ACTIVE
        );
    }
}
