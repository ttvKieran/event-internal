package com.example.registration_service.infrastructure.persistence.repository;

import com.example.registration_service.infrastructure.persistence.entity.ProcessedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface JpaProcessedMessageRepository extends JpaRepository<ProcessedMessageEntity, String> {
    @Modifying
    @Query(value = """
        INSERT INTO processed_messages (message_id, processed_at)
        VALUES (:messageId, :processedAt)
        ON CONFLICT (message_id) DO NOTHING
        """, nativeQuery = true)
    int insertIfAbsent(
        @Param("messageId") String messageId,
        @Param("processedAt") Instant processedAt
    );
}
