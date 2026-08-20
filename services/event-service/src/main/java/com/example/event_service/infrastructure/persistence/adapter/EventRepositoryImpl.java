package com.example.event_service.infrastructure.persistence.adapter;

import com.example.event_service.domain.model.aggregate.Event;
import com.example.event_service.domain.model.valueobject.EventStatus;
import com.example.event_service.domain.repository.EventRepository;
import com.example.event_service.infrastructure.persistence.entity.EventEntity;
import com.example.event_service.infrastructure.persistence.mapper.EventMapper;
import com.example.event_service.infrastructure.persistence.repository.JpaEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public List<Event> findEvents(EventStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Convert Enum sang String để truyền vào JPA
        String statusStr = (status != null) ? status.getCode() : null;
        return jpaRepository.findByStatusWithPagination(statusStr, pageable)
            .getContent().stream()
            .map(eventMapper::toDomain)
            .collect(Collectors.toList());
    }
}
