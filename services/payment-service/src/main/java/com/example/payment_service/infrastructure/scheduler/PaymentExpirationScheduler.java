package com.example.payment_service.infrastructure.scheduler;

import com.example.payment_service.application.port.in.PaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentExpirationScheduler {

    private final PaymentUseCase paymentUseCase;

    /**
     * Chạy mỗi 5 phút (fixedRate = 5 * 60 * 1000 ms).
     * Quét toàn bộ giao dịch PENDING quá hạn → markAsExpired() → Saga rollback vé.
     *
     * Cron biểu thức: "0 *\/5 * * * *" (Mỗi 5 phút đầu giờ)
     * Có thể điều chỉnh qua: payment.expiration.scheduler-cron trong application.yaml
     */
    @Scheduled(cron = "${payment.expiration.scheduler-cron:0 */5 * * * *}")
    public void expireStalePayments() {
        log.info("[Scheduler] Bắt đầu quét giao dịch thanh toán quá hạn...");
        paymentUseCase.expireStalePayments();
        log.info("[Scheduler] Hoàn tất quét giao dịch quá hạn.");
    }
}
