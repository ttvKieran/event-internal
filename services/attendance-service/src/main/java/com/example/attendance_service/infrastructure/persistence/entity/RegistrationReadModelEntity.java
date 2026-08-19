package com.example.attendance_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "registration_read_model")
@Data
public class RegistrationReadModelEntity {
    @Id
    private UUID registrationId;

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "employee_id")
    private UUID employeeId;

    private String status;
}
