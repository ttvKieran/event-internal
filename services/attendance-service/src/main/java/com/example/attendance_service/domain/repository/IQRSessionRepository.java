package com.example.attendance_service.domain.repository;

import com.example.attendance_service.domain.model.aggregate.QRSession;
import java.util.Optional;

public interface IQRSessionRepository {
    QRSession save(QRSession session);
    Optional<QRSession> findActiveSessionByEventId(String eventId);
}
