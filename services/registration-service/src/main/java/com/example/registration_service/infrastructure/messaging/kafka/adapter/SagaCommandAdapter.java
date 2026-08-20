package com.example.registration_service.infrastructure.messaging.kafka.adapter;

import com.example.registration_service.application.dto.message.ConfirmPaidRegistrationCommandPayload;
import com.example.registration_service.application.dto.message.RollbackPaidRegistrationCommandPayload;
import com.example.registration_service.application.port.out.SagaCommandPort;
import com.example.registration_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.example.registration_service.infrastructure.persistence.repository.JpaOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaCommandAdapter implements SagaCommandPort {

    private final JpaOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void sendConfirmCommand(UUID registrationId, UUID paymentId) {
        ConfirmPaidRegistrationCommandPayload payload =
            new ConfirmPaidRegistrationCommandPayload(registrationId, paymentId);

        insertOutbox(registrationId, "ConfirmPaidRegistrationCommand", payload);
        log.info("[SagaManager] Phát lệnh ConfirmPaidRegistrationCommand cho registrationId={}", registrationId);
    }

    @Override
    public void sendRollbackCommand(UUID registrationId, String reason) {
        RollbackPaidRegistrationCommandPayload payload =
            new RollbackPaidRegistrationCommandPayload(registrationId, reason);

        insertOutbox(registrationId, "RollbackPaidRegistrationCommand", payload);
        log.info("[SagaManager] Phát lệnh RollbackPaidRegistrationCommand cho registrationId={}, reason={}",
            registrationId, reason);
    }

    private void insertOutbox(UUID registrationId, String commandType, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            OutboxEventEntity outbox = OutboxEventEntity.of(
                "Registration",
                registrationId.toString(),
                commandType,
                json
            );
            outboxRepository.save(outbox);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi serialize Outbox Command: " + commandType, e);
        }
    }
}
