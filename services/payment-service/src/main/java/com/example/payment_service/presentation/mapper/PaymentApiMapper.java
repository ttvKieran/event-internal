package com.example.payment_service.presentation.mapper;

import com.example.payment_service.domain.model.aggregate.PaymentTransaction;
import com.example.payment_service.presentation.dto.response.PaymentResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PaymentApiMapper {

    public PaymentResponseDTO toResponseDto(PaymentTransaction transaction) {
        if (transaction == null) return null;
        return PaymentResponseDTO.builder()
            .paymentId(transaction.getPaymentId())
            .registrationId(transaction.getRegistrationId())
            .paymentUrl(transaction.getPaymentUrl())
            .status(transaction.getStatus() != null ? transaction.getStatus().getCode() : "PENDING")
            .amount(transaction.getAmount() != null ? transaction.getAmount().getAmount() : null)
            .build();
    }
}
