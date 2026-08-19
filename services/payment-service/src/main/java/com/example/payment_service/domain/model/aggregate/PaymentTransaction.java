package com.example.payment_service.domain.model.aggregate;

import com.example.payment_service.domain.model.valueobject.Money;
import com.example.payment_service.domain.model.valueobject.PaymentProvider;
import com.example.payment_service.domain.model.valueobject.PaymentStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "paymentId")
public class PaymentTransaction {

    private UUID paymentId;
    private UUID registrationId;
    private UUID campaignId;
    private Money amount;
    private PaymentStatus status;
    private PaymentProvider provider;
    private String providerTxnId;  // Mã giao dịch VNPay cấp
    private String paymentUrl;     // Link QR trả về cho User
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PaymentTransaction() {}

    public static PaymentTransaction create(
        UUID registrationId, UUID campaignId,
        Money amount, PaymentProvider provider) {
        PaymentTransaction txn = new PaymentTransaction();
        txn.paymentId      = UUID.randomUUID();
        txn.registrationId = registrationId;
        txn.campaignId     = campaignId;
        txn.amount         = amount;
        txn.status         = PaymentStatus.PENDING;
        txn.provider       = provider;
        txn.createdAt      = LocalDateTime.now();
        txn.updatedAt      = LocalDateTime.now();
        return txn;
    }

    public static PaymentTransaction reconstitute(
        UUID paymentId, UUID registrationId, UUID campaignId,
        Money amount, PaymentStatus status, PaymentProvider provider,
        String providerTxnId, String paymentUrl,
        LocalDateTime createdAt, LocalDateTime updatedAt) {

        PaymentTransaction txn = new PaymentTransaction();
        txn.paymentId      = paymentId;
        txn.registrationId = registrationId;
        txn.campaignId     = campaignId;
        txn.amount         = amount;
        txn.status         = status;
        txn.provider       = provider;
        txn.providerTxnId  = providerTxnId;
        txn.paymentUrl     = paymentUrl;
        txn.createdAt      = createdAt;
        txn.updatedAt      = updatedAt;
        return txn;
    }

    // VNPay IPN callback báo thành công
    public void markAsSuccess(String providerTxnId) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                "Chỉ giao dịch PENDING mới được xác nhận thành công. Hiện tại: " + this.status.getCode());
        }
        this.status        = PaymentStatus.SUCCESS;
        this.providerTxnId = providerTxnId;
        this.updatedAt     = LocalDateTime.now();
    }

    // VNPay IPN callback báo thất bại
    public void markAsFailed() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                "Chỉ giao dịch PENDING mới được đánh dấu thất bại. Hiện tại: " + this.status.getCode());
        }
        this.status    = PaymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    // Scheduler gọi sau 15 phút không có kết quả
    public void markAsExpired() {
        if (this.status != PaymentStatus.PENDING) return;
        this.status    = PaymentStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    // Gán link thanh toán sau khi tạo từ VNPay SDK
    public void assignPaymentUrl(String url) {
        this.paymentUrl = url;
        this.updatedAt  = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }
}
