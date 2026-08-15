// Đổi dòng package thành thế này (thêm chữ adapter vào cuối)
package com.example.event_service.infrastructure.persistence.adapter;

import com.example.event_service.domain.model.aggregate.Event;
import com.example.event_service.domain.repository.EventRepository;
import com.example.event_service.infrastructure.persistence.entity.EventEntity;
import com.example.event_service.infrastructure.persistence.mapper.EventMapper;
import com.example.event_service.infrastructure.persistence.repository.JpaEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final JpaEventRepository jpaRepository;
    private final EventMapper eventMapper;

    @Override
    public Event save(Event event) {
        EventEntity entity = eventMapper.toEntity(event);
        EventEntity savedEntity = jpaRepository.save(entity);
        return eventMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Event> findById(UUID eventId) {
        return jpaRepository.findById(eventId)
            .map(eventMapper::toDomain);
    }
}
