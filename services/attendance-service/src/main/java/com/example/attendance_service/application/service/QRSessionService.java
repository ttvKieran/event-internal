package com.example.attendance_service.application.service;
import com.example.attendance_service.application.port.in.ManageQRSessionUseCase;
import com.example.attendance_service.application.port.out.AttendanceMessagePort;
import com.example.attendance_service.application.dto.message.QRSessionStartedEventPayload;
import com.example.attendance_service.domain.model.aggregate.QRSession;
import com.example.attendance_service.domain.repository.IQRSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class QRSessionService implements ManageQRSessionUseCase {
    private final IQRSessionRepository qrSessionRepo;
    private final AttendanceMessagePort messagePort;
    public QRSessionService(IQRSessionRepository qrSessionRepo, AttendanceMessagePort messagePort) {
        this.qrSessionRepo = qrSessionRepo;
        this.messagePort = messagePort;
    }
    @Override
    @Transactional
    public QRSession createNewSession(String eventId) {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codes.add("QR_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        QRSession session = QRSession.builder()
            .id(UUID.randomUUID().toString())
            .eventId(eventId)
            .qrCodes(codes)
            .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
            .build();
        qrSessionRepo.save(session);
        messagePort.publishQRSessionStarted(QRSessionStartedEventPayload.builder()
                .sessionId(session.getId())
                .eventId(eventId)
                .expiresAt(session.getExpiresAt())
                .build());
        return session;
    }

    @Override
    public java.util.Optional<QRSession> getActiveSession(String eventId) {
        return qrSessionRepo.findActiveSessionByEventId(eventId);
    }
}
