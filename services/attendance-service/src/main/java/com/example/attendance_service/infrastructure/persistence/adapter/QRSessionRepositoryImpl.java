package com.example.attendance_service.infrastructure.persistence.adapter;
import com.example.attendance_service.domain.model.aggregate.QRSession;
import com.example.attendance_service.domain.repository.IQRSessionRepository;
import com.example.attendance_service.infrastructure.persistence.entity.QRSessionEntity;
import com.example.attendance_service.infrastructure.persistence.repository.QRSessionSpringRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;
@Component
public class QRSessionRepositoryImpl implements IQRSessionRepository {
    private final QRSessionSpringRepository repo;
    public QRSessionRepositoryImpl(QRSessionSpringRepository repo) { this.repo = repo; }
    @Override
    public QRSession save(QRSession session) {
        QRSessionEntity entity = new QRSessionEntity();
        if (session.getId() != null) entity.setId(UUID.fromString(session.getId()));
        else entity.setId(UUID.randomUUID());
        entity.setEventId(UUID.fromString(session.getEventId()));
        entity.setQrCodes(session.getQrCodes());
        entity.setExpiresAt(session.getExpiresAt());
        repo.save(entity);
        return session;
    }
    @Override
    public Optional<QRSession> findActiveSessionByEventId(String eventId) {
        QRSessionEntity entity = repo.findFirstByEventIdOrderByExpiresAtDesc(UUID.fromString(eventId));
        if (entity != null) {
            return Optional.of(QRSession.builder()
                .id(entity.getId().toString())
                .eventId(entity.getEventId().toString())
                .qrCodes(entity.getQrCodes())
                .expiresAt(entity.getExpiresAt())
                .build());
        }
        return Optional.empty();
    }
}
