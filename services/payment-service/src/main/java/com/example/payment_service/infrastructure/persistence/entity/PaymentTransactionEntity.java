package com.example.payment_service.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
public class PaymentTransactionEntity extends BaseEntity {

    @Id
    private UUID paymentId;

    @Column(nullable = false)
    private UUID registrationId;

    @Column(nullable = false)
    private UUID campaignId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 20)
    private String status;        // Lưu PaymentStatus.code

    @Column(nullable = false, length = 20)
    private String provider;      // Lưu PaymentProvider.code

    @Column(length = 100)
    private String providerTxnId; // Mã giao dịch VNPay cấp (null khi còn PENDING)

    @Column(length = 500)
    private String paymentUrl;    // Link QR thanh toán
}
