package com.example.registration_service.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registration_campaigns")
@Getter
@Setter
public class RegistrationCampaignEntity extends BaseEntity {

    @Id
    private UUID campaignId;

    private String ticketType;
    private Integer maxParticipants;
    private Integer currentParticipants;

    private LocalDateTime openAt;
    private LocalDateTime closeAt;

    private String status;
}
