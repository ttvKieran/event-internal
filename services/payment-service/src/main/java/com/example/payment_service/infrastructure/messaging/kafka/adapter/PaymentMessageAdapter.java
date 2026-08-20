package com.example.payment_service.infrastructure.messaging.kafka.adapter;

import com.example.payment_service.application.dto.message.PaymentFailedEventPayload;
import com.example.payment_service.application.dto.message.PaymentSucceededEventPayload;
import com.example.payment_service.application.port.out.PaymentMessagePort;
import com.example.payment_service.domain.model.aggregate.PaymentTransaction;
import com.example.payment_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.example.payment_service.infrastructure.persistence.repository.JpaOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageAdapter implements PaymentMessagePort {

    private final JpaOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishPaymentSucceeded(PaymentTransaction txn) {
        PaymentSucceededEventPayload payload = new PaymentSucceededEventPayload(
            txn.getPaymentId(),
            txn.getRegistrationId(),
            txn.getCampaignId(),
            txn.getAmount().getAmount(),
            txn.getProviderTxnId() != null ? txn.getProviderTxnId() : "",
            txn.getUpdatedAt().toString()
        );
        insertOutbox(txn, "PaymentSucceededEvent", payload);
    }

    @Override
    public void publishPaymentFailed(PaymentTransaction txn, String reason) {
        PaymentFailedEventPayload payload = new PaymentFailedEventPayload(
            txn.getPaymentId(),
            txn.getRegistrationId(),
            txn.getCampaignId(),
            reason,
            txn.getUpdatedAt().toString()
        );
        insertOutbox(txn, "PaymentFailedEvent", payload);
    }

    private void insertOutbox(PaymentTransaction txn, String eventType, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            OutboxEventEntity outbox = OutboxEventEntity.of(
                "PaymentTransaction",
                txn.getPaymentId().toString(),
                eventType,
                json
            );
            outboxRepository.save(outbox);
            log.info("[Outbox] Đã ghi sự kiện {} cho paymentId={}", eventType, txn.getPaymentId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi serialize Outbox Event: " + eventType, e);
        }
    }
}
