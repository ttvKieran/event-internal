package com.example.registration_service.infrastructure.persistence.repository;

import com.example.registration_service.domain.model.aggregate.Registration;
import com.example.registration_service.domain.model.valueobject.RegistrationStatus;
import com.example.registration_service.infrastructure.persistence.entity.RegistrationEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRegistrationRepository extends JpaRepository<RegistrationEntity, UUID> {
    @Query("SELECT r FROM RegistrationEntity r WHERE " +
        "(:campaignId IS NULL OR r.campaignId = :campaignId) AND " +
        "(:userId IS NULL OR r.userId = :userId) AND " +
        "(:status IS NULL OR r.status = :status)")
    List<RegistrationEntity> findByFilters(
        @Param("campaignId") UUID campaignId,
        @Param("userId") UUID userId,
        @Param("status") String status
    );

    @Query("SELECT COUNT(r) FROM RegistrationEntity r WHERE r.campaignId = :campaignId AND r.status IN :statuses")
    long countByCampaignIdAndStatusIn(
        @Param("campaignId") UUID campaignId,
        @Param("statuses") java.util.List<String> statuses
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("""
    SELECT r
    FROM RegistrationEntity r
    WHERE r.id = :id
    """)
    Optional<RegistrationEntity> findByIdForUpdate(
        @Param("id") UUID id
    );
}
