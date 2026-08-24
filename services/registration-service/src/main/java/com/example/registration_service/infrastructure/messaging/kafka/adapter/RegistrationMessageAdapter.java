package com.example.registration_service.infrastructure.messaging.kafka.adapter;

import com.example.registration_service.application.model.command.ReserveTicketCommand;
import com.example.registration_service.infrastructure.messaging.kafka.dto.outbound.AsyncReserveTicketCommandPayload;
import com.example.registration_service.infrastructure.messaging.kafka.dto.outbound.PaidRegistrationRolledBackEventPayload;
import com.example.registration_service.infrastructure.messaging.kafka.dto.outbound.RegistrationConfirmedEventPayload;
import com.example.registration_service.infrastructure.messaging.kafka.dto.outbound.RegistrationRequestedEventPayload;
import com.example.registration_service.application.port.out.RegistrationMessagePort;
import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.example.registration_service.infrastructure.persistence.repository.JpaOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationMessageAdapter implements RegistrationMessagePort {

    private final JpaOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishRegistrationRequested(Registration reg, java.math.BigDecimal amount, String provider) {
        RegistrationRequestedEventPayload payload = new RegistrationRequestedEventPayload(
            reg.getRegistrationId(),
            reg.getCampaignId(),
            reg.getUserId(),
            amount,
            provider,
            reg.getRegisteredAt().toString()
        );
        insertOutbox(reg, "RegistrationRequestedEvent", payload);
    }

    @Override
    public void publishRegistrationConfirmed(Registration reg) {
        RegistrationConfirmedEventPayload payload = new RegistrationConfirmedEventPayload(
            reg.getRegistrationId(),
            reg.getCampaignId(),
            reg.getUserId(),
            reg.getRegisteredAt().toString()
        );
        insertOutbox(reg, "RegistrationConfirmedEvent", payload);
    }

    @Override
    public void publishPaidRegistrationRolledBack(Registration reg, String reason) {
        PaidRegistrationRolledBackEventPayload payload = new PaidRegistrationRolledBackEventPayload(
            reg.getRegistrationId(),
            reg.getCampaignId(),
            reason,
            LocalDateTime.now().toString()
        );
        insertOutbox(reg, "PaidRegistrationRolledBackEvent", payload);
    }

    @Override
    public void publishAsyncReserveTicketCommand(ReserveTicketCommand command) {
        AsyncReserveTicketCommandPayload.ProviderPayload provider =
            command.providerCode() == null
                ? null
                : new AsyncReserveTicketCommandPayload.ProviderPayload(
                    command.providerCode()
                );

        AsyncReserveTicketCommandPayload payload =
            new AsyncReserveTicketCommandPayload(
                command.campaignId(),
                command.userId(),
                provider
            );

        try {
            String json = objectMapper.writeValueAsString(payload);
            OutboxEventEntity outbox =
                OutboxEventEntity.of(
                    "Registration",
                    command.campaignId().toString(),
                    "AsyncReserveTicketCommand",
                    json
                );
            outboxRepository.save(outbox);
            log.info("[Outbox] Đã lưu lệnh AsyncReserveTicketCommand");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi serialize AsyncReserveTicketCommand", e);
        }
    }

    private void insertOutbox(Registration reg, String eventType, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            OutboxEventEntity outbox = OutboxEventEntity.of(
                "Registration",
                reg.getRegistrationId().toString(),
                eventType,
                json
            );
            outboxRepository.save(outbox);
            log.info("[Outbox] Đã lưu sự kiện {}", eventType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi serialize Outbox Event: " + eventType, e);
        }
    }
}
