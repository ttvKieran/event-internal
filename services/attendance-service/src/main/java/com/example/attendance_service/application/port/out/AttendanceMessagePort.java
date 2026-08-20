package com.example.attendance_service.application.port.out;

import com.example.attendance_service.application.dto.message.DuplicateCheckInRejectedEventPayload;
import com.example.attendance_service.application.dto.message.InvalidQrScanRejectedEventPayload;
import com.example.attendance_service.application.dto.message.ParticipantCheckedInEventPayload;
import com.example.attendance_service.application.dto.message.QRSessionStartedEventPayload;

public interface AttendanceMessagePort {
    void publishQRSessionStarted(QRSessionStartedEventPayload payload);
    void publishParticipantCheckedIn(ParticipantCheckedInEventPayload payload);
    void publishDuplicateCheckInRejected(DuplicateCheckInRejectedEventPayload payload);
    void publishInvalidQrScanRejected(InvalidQrScanRejectedEventPayload payload);
}
