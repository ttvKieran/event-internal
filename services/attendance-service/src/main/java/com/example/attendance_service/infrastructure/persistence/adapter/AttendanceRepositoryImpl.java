package com.example.attendance_service.infrastructure.persistence.adapter;
import com.example.attendance_service.domain.model.aggregate.AttendanceRecord;
import com.example.attendance_service.domain.repository.IAttendanceRepository;
import com.example.attendance_service.infrastructure.persistence.entity.AttendanceRecordEntity;
import com.example.attendance_service.infrastructure.persistence.repository.AttendanceRecordSpringRepository;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class AttendanceRepositoryImpl implements IAttendanceRepository {
    private final AttendanceRecordSpringRepository repo;
    public AttendanceRepositoryImpl(AttendanceRecordSpringRepository repo) { this.repo = repo; }
    @Override
    public AttendanceRecord save(AttendanceRecord record) {
        AttendanceRecordEntity entity = new AttendanceRecordEntity();
        if (record.getId() != null) entity.setId(UUID.fromString(record.getId()));
        else entity.setId(UUID.randomUUID());
        entity.setEventId(UUID.fromString(record.getEventId()));
        entity.setEmployeeId(UUID.fromString(record.getEmployeeId()));
        entity.setStatus(record.getStatus());
        entity.setScannedAt(record.getScannedAt());
        repo.save(entity);
        return record;
    }
    @Override
    public boolean hasCheckedIn(String eventId, String employeeId) {
        return repo.existsByEventIdAndEmployeeId(UUID.fromString(eventId), UUID.fromString(employeeId));
    }
}
