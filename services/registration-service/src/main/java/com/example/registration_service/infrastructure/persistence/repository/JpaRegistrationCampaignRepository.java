package com.example.registration_service.infrastructure.persistence.repository;

import com.example.registration_service.infrastructure.persistence.entity.RegistrationCampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaRegistrationCampaignRepository extends JpaRepository<RegistrationCampaignEntity, UUID> {
}
