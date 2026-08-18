package com.example.event_service.domain.repository;

import com.example.event_service.domain.model.aggregate.Event;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    // Lưu hoặc Cập nhật sự kiện
    Event save(Event event);

    // Tìm sự kiện theo ID
    Optional<Event> findById(UUID eventId);
}
