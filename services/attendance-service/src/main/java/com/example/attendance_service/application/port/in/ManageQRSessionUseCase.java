package com.example.attendance_service.application.port.in;
import com.example.attendance_service.domain.model.aggregate.QRSession;
import java.util.Optional;
public interface ManageQRSessionUseCase {
    QRSession createNewSession(String eventId);
    Optional<QRSession> getActiveSession(String eventId);
}
