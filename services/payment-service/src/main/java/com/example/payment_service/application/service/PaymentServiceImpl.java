package com.example.payment_service.application.service;

import com.example.payment_service.application.dto.command.VnPayIpnCommand;
import com.example.payment_service.application.dto.message.RegistrationRequestedPayload;
import com.example.payment_service.application.port.in.PaymentUseCase;
import com.example.payment_service.application.port.out.PaymentGatewayPort;
import com.example.payment_service.application.port.out.PaymentMessagePort;
import com.example.payment_service.domain.model.aggregate.PaymentTransaction;
import com.example.payment_service.domain.model.valueobject.Money;
import com.example.payment_service.domain.model.valueobject.PaymentProvider;
import com.example.payment_service.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentMessagePort messagePort;
    private final PaymentGatewayPort paymentGatewayPort;

    @Value("${payment.expiration.timeout-minutes:15}")
    private int paymentTimeoutMinutes;

    @Override
    @Transactional
    public void handleRegistrationRequested(RegistrationRequestedPayload payload) {

        paymentRepository.findByRegistrationId(payload.getRegistrationId()).ifPresent(existing -> {
            log.warn("[Payment] Đã tồn tại giao dịch cho registrationId={}. Bỏ qua.", payload.getRegistrationId());
            throw new IllegalStateException("Giao dịch đã được khởi tạo từ trước");
        });

        // Tạo giao dịch mới
        PaymentTransaction transaction = PaymentTransaction.create(
            payload.getRegistrationId(),
            payload.getCampaignId(),
            Money.of(payload.getAmount()),
            PaymentProvider.of(payload.getProvider())
        );

        String orderInfo = "Thanh toan ve su kien " + transaction.getCampaignId();
        String paymentUrl = paymentGatewayPort.createPaymentUrl(
            transaction.getRegistrationId(),
            transaction.getAmount(),
            orderInfo
        );
        transaction.assignPaymentUrl(paymentUrl);

        paymentRepository.save(transaction);

        log.info("[Payment] Đã tạo giao dịch paymentId={} cho registrationId={}",
            transaction.getPaymentId(), transaction.getRegistrationId());
    }

    @Override
    @Transactional
    public void handleVnPayIpn(VnPayIpnCommand command) {

        UUID registrationId = UUID.fromString(command.getVnpTxnRef());
        PaymentTransaction transaction = paymentRepository.findByRegistrationId(registrationId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Không tìm thấy giao dịch cho registrationId=" + registrationId));

        if ("00".equals(command.getVnpResponseCode())) {
            transaction.markAsSuccess(command.getVnpTransactionNo());
            paymentRepository.save(transaction);
            messagePort.publishPaymentSucceeded(transaction);
            log.info("[Payment] Thanh toán thành công. registrationId={}", registrationId);
        } else {
            transaction.markAsFailed();
            paymentRepository.save(transaction);
            messagePort.publishPaymentFailed(transaction, "PAYMENT_FAILED");
            log.warn("[Payment] Thanh toán thất bại. registrationId={}, responseCode={}",
                registrationId, command.getVnpResponseCode());
        }
    }

    @Override
    @Transactional
    public void expireStalePayments() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(paymentTimeoutMinutes);
        List<PaymentTransaction> staleList = paymentRepository.findPendingBefore(threshold);

        if (staleList.isEmpty()) return;

        log.info("[Payment] Scheduler phát hiện {} giao dịch quá hạn. Đang xử lý...", staleList.size());

        for (PaymentTransaction txn : staleList) {
            txn.markAsExpired();
            paymentRepository.save(txn);
            messagePort.publishPaymentFailed(txn, "PAYMENT_TIMED_OUT");
            log.info("[Payment] Đã hủy giao dịch hết hạn. registrationId={}", txn.getRegistrationId());
        }
    }

    @Override
    public PaymentTransaction getPaymentByRegistrationId(UUID registrationId) {
        return paymentRepository.findByRegistrationId(registrationId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch cho registrationId=" + registrationId));
    }

}
