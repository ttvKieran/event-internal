package com.example.attendance_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity @Table(name = "qr_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRSessionEntity {
    @Id
    private UUID id;

    @Column(name = "event_id")
    private UUID eventId;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "qr_codes", columnDefinition = "jsonb")
    private List<String> qrCodes;

    private Instant expiresAt;
}
