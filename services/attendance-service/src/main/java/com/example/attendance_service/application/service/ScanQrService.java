package com.example.attendance_service.application.service;

import com.example.attendance_service.application.dto.message.DuplicateCheckInRejectedEventPayload;
import com.example.attendance_service.application.dto.message.InvalidQrScanRejectedEventPayload;
import com.example.attendance_service.application.dto.message.ParticipantCheckedInEventPayload;
import com.example.attendance_service.application.port.in.ScanQrUseCase;
import com.example.attendance_service.application.port.out.AttendanceMessagePort;
import com.example.attendance_service.domain.model.aggregate.AttendanceRecord;
import com.example.attendance_service.domain.model.valueobject.AttendanceStatus;
import com.example.attendance_service.domain.model.aggregate.QRSession;
import com.example.attendance_service.domain.repository.IAttendanceRepository;
import com.example.attendance_service.domain.repository.IQRSessionRepository;
import com.example.attendance_service.domain.repository.IRegistrationReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ScanQrService implements ScanQrUseCase {
    private final IQRSessionRepository qrSessionRepo;
    private final IAttendanceRepository attendanceRepo;
    private final IRegistrationReadRepository registrationRepo;
    private final AttendanceMessagePort messagePort;

    public ScanQrService(IQRSessionRepository qrSessionRepo, IAttendanceRepository attendanceRepo,
                         IRegistrationReadRepository registrationRepo, AttendanceMessagePort messagePort) {
        this.qrSessionRepo = qrSessionRepo;
        this.attendanceRepo = attendanceRepo;
        this.registrationRepo = registrationRepo;
        this.messagePort = messagePort;
    }

    @Override
    @Transactional
    public String scanCheckIn(String eventId, String employeeId, String qrToken) {
        QRSession session = qrSessionRepo.findActiveSessionByEventId(eventId)
            .orElseThrow(() -> new RuntimeException("QR session not found"));

        if (!session.isValidToken(qrToken)) {
            messagePort.publishInvalidQrScanRejected(InvalidQrScanRejectedEventPayload.builder()
                .eventId(eventId)
                .scannedToken(qrToken)
                .reason("Invalid or expired token")
                .attemptedAt(Instant.now())
                .build());
            throw new RuntimeException("Invalid QR!");
        }

        if (!registrationRepo.isRegistered(eventId, employeeId)) {
            throw new RuntimeException("You have not registered for this event..");
        }

        if (attendanceRepo.hasCheckedIn(eventId, employeeId)) {
            messagePort.publishDuplicateCheckInRejected(DuplicateCheckInRejectedEventPayload.builder()
                .eventId(eventId)
                .employeeId(employeeId)
                .attemptedAt(Instant.now())
                .build());
            throw new RuntimeException("You have already checked in.");
        }

        AttendanceRecord record = AttendanceRecord.builder()
            .id(UUID.randomUUID().toString())
            .eventId(eventId)
            .employeeId(employeeId)
            .status(AttendanceStatus.CHECK_IN)
            .scannedAt(Instant.now())
            .build();
        attendanceRepo.save(record);

        messagePort.publishParticipantCheckedIn(ParticipantCheckedInEventPayload.builder()
            .eventId(eventId)
            .employeeId(employeeId)
            .checkedInAt(Instant.now())
            .build());

        return "Check in successfully";
    }
}
