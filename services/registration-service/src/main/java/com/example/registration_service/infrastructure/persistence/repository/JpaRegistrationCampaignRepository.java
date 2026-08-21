package com.example.registration_service.infrastructure.persistence.repository;

import com.example.registration_service.infrastructure.persistence.entity.RegistrationCampaignEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRegistrationCampaignRepository extends JpaRepository<RegistrationCampaignEntity, UUID> {
    // QueryHints: Chỉ chờ tối đa 3 giây. Quá 3s tự văng PessimisticLockException
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT c FROM RegistrationCampaignEntity c WHERE c.id = :id")
    Optional<RegistrationCampaignEntity> findByIdForUpdate(@Param("id") UUID id);
}
