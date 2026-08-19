package com.example.attendance_service.infrastructure.messaging.kafka;
import com.example.attendance_service.application.port.out.AttendanceMessagePort;
import com.example.attendance_service.application.dto.message.*;
import com.example.attendance_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.example.attendance_service.infrastructure.persistence.repository.OutboxEventSpringRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;
@Component
public class AttendanceMessageAdapter implements AttendanceMessagePort {
    private final OutboxEventSpringRepository outboxRepo;
    private final ObjectMapper objectMapper;
    public AttendanceMessageAdapter(OutboxEventSpringRepository outboxRepo, ObjectMapper objectMapper) {
        this.outboxRepo = outboxRepo;
        this.objectMapper = objectMapper;
    }
    private void saveOutboxEvent(String aggregateType, String aggregateId, String type, Object payload) {
        try {
            OutboxEventEntity entity = new OutboxEventEntity();
            entity.setId(UUID.randomUUID());
            entity.setCorrelationId(UUID.randomUUID());
            entity.setAggregateType(aggregateType);
            entity.setAggregateId(aggregateId);
            entity.setType(type);
            entity.setPayload(objectMapper.writeValueAsString(payload));
            entity.setCreatedAt(Instant.now());
            outboxRepo.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Serialization error", e);
        }
    }
    @Override
    public void publishQRSessionStarted(QRSessionStartedEventPayload payload) {
        saveOutboxEvent("QRSession", payload.getSessionId(), "QRSessionStartedEvent", payload);
    }
    @Override
    public void publishParticipantCheckedIn(ParticipantCheckedInEventPayload payload) {
        saveOutboxEvent("Attendance", payload.getEmployeeId(), "ParticipantCheckedInEvent", payload);
    }
    @Override
    public void publishDuplicateCheckInRejected(DuplicateCheckInRejectedEventPayload payload) {
        saveOutboxEvent("Attendance", payload.getEmployeeId(), "DuplicateCheckInRejectedEvent", payload);
    }
    @Override
    public void publishInvalidQrScanRejected(InvalidQrScanRejectedEventPayload payload) {
        saveOutboxEvent("QRSession", payload.getEventId(), "InvalidQrScanRejectedEvent", payload);
    }
}
