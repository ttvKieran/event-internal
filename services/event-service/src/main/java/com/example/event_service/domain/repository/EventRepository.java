package com.example.event_service.domain.repository;

import com.example.event_service.domain.model.aggregate.Event;
import com.example.event_service.domain.model.valueobject.EventStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    // Lưu hoặc Cập nhật sự kiện
    Event save(Event event);

    // Tìm sự kiện theo ID
    Optional<Event> findById(UUID eventId);

    // Lấy danh sách sự kiện
    List<Event> findEvents(EventStatus status, int page, int size);
}
