package com.example.registration_service.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registrations")
@Getter
@Setter
public class RegistrationEntity extends BaseEntity {

    @Id
    private UUID registrationId;

    private UUID campaignId;
    private UUID userId;

    private String status;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    private LocalDateTime registeredAt;

}
