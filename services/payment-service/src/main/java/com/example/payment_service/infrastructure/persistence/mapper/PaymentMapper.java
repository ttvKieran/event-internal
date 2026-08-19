package com.example.payment_service.infrastructure.persistence.mapper;

import com.example.payment_service.domain.model.aggregate.PaymentTransaction;
import com.example.payment_service.domain.model.valueobject.Money;
import com.example.payment_service.domain.model.valueobject.PaymentProvider;
import com.example.payment_service.domain.model.valueobject.PaymentStatus;
import com.example.payment_service.infrastructure.persistence.entity.PaymentTransactionEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentMapper {

    public PaymentTransactionEntity toEntity(PaymentTransaction domain) {
        if (domain == null) return null;

        PaymentTransactionEntity entity = new PaymentTransactionEntity();
        entity.setPaymentId(domain.getPaymentId());
        entity.setRegistrationId(domain.getRegistrationId());
        entity.setCampaignId(domain.getCampaignId());
        entity.setAmount(domain.getAmount().getAmount());
        entity.setCurrency(domain.getAmount().getCurrency());
        entity.setStatus(domain.getStatus().getCode());
        entity.setProvider(domain.getProvider().getCode());
        entity.setProviderTxnId(domain.getProviderTxnId());
        entity.setPaymentUrl(domain.getPaymentUrl());
        return entity;
    }

    public PaymentTransaction toDomain(PaymentTransactionEntity entity) {
        if (entity == null) return null;

        return PaymentTransaction.reconstitute(
            entity.getPaymentId(),
            entity.getRegistrationId(),
            entity.getCampaignId(),
            Money.of(entity.getAmount() != null ? entity.getAmount() : BigDecimal.ZERO,
                entity.getCurrency() != null ? entity.getCurrency() : "VND"),
            PaymentStatus.of(entity.getStatus()),
            PaymentProvider.of(entity.getProvider()),
            entity.getProviderTxnId(),
            entity.getPaymentUrl(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
