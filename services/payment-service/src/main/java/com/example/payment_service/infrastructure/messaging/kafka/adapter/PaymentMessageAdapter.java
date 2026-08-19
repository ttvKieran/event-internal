package com.example.payment_service.infrastructure.messaging.kafka.adapter;

import com.example.payment_service.application.port.out.PaymentMessagePort;
import com.example.payment_service.domain.model.aggregate.PaymentTransaction;
import com.example.payment_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.example.payment_service.infrastructure.persistence.repository.JpaOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageAdapter implements PaymentMessagePort {

    private final JpaOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishPaymentSucceeded(PaymentTransaction transaction) {
        Map<String, Object> payload = Map.of(
            "paymentId",      transaction.getPaymentId().toString(),
            "registrationId", transaction.getRegistrationId().toString(),
            "campaignId",     transaction.getCampaignId().toString(),
            "amount",         transaction.getAmount().getAmount(),
            "providerTxnId",  transaction.getProviderTxnId() != null ? transaction.getProviderTxnId() : "",
            "paidAt",         transaction.getUpdatedAt().toString()
        );
        insertOutbox(transaction, "PaymentSucceededEvent", payload);
    }

    @Override
    public void publishPaymentFailed(PaymentTransaction transaction, String reason) {
        Map<String, Object> payload = Map.of(
            "paymentId",      transaction.getPaymentId().toString(),
            "registrationId", transaction.getRegistrationId().toString(),
            "campaignId",     transaction.getCampaignId().toString(),
            "reason",         reason,
            "failedAt",       transaction.getUpdatedAt().toString()
        );
        insertOutbox(transaction, "PaymentFailedEvent", payload);
    }

    private void insertOutbox(PaymentTransaction transaction, String eventType, Map<String, Object> payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            OutboxEventEntity outbox = OutboxEventEntity.of(
                "PaymentTransaction",
                transaction.getPaymentId().toString(),
                eventType,
                json
            );
            outboxRepository.save(outbox);
            log.info("[Outbox] Đã ghi sự kiện {} cho paymentId={}", eventType, transaction.getPaymentId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi serialize Outbox Event: " + eventType, e);
        }
    }
}
