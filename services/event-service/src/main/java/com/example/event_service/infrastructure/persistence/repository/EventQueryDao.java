package com.example.event_service.infrastructure.persistence.repository;

import com.example.event_service.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface EventQueryDao extends JpaRepository<EventEntity, UUID> {

    @Query("SELECT e.id as id, e.title as title, e.status as status, e.ticketTypeCode as ticketTypeCode, e.price as price, e.startTime as startTime FROM EventEntity e WHERE e.id = :eventId")
    Optional<EventSummaryView> fetchEventDetails(@Param("eventId") UUID eventId);
}
