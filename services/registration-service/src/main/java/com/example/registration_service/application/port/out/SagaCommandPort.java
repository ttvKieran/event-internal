package com.example.registration_service.application.port.out;

import java.util.UUID;

/**
 * Port ra ngoài để RegistrationSagaManager phát lệnh điều phối Saga.
 * Các Command này được gửi lên topic "registration-commands".
 */
public interface SagaCommandPort {

    /**
     * Phát lệnh chốt vé sau khi Payment báo thành công.
     * → RegistrationCommandListener nhận và gọi confirmRegistration()
     */
    void sendConfirmCommand(UUID registrationId, UUID paymentId);

    /**
     * Phát lệnh hoàn trả slot vé sau khi Payment báo thất bại/timeout.
     * → RegistrationCommandListener nhận và gọi cancelRegistration()
     */
    void sendRollbackCommand(UUID registrationId, String reason);
}
