package com.example.attendance_service.infrastructure.persistence.adapter;
import com.example.attendance_service.domain.repository.IRegistrationReadRepository;
import com.example.attendance_service.infrastructure.persistence.repository.RegistrationReadModelSpringRepository;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class RegistrationReadRepositoryImpl implements IRegistrationReadRepository {
    private final RegistrationReadModelSpringRepository repo;
    public RegistrationReadRepositoryImpl(RegistrationReadModelSpringRepository repo) { this.repo = repo; }
    @Override
    public boolean isRegistered(String eventId, String employeeId) {
        return repo.existsByEventIdAndEmployeeIdAndStatus(UUID.fromString(eventId), UUID.fromString(employeeId), "CONFIRMED");
    }
}
