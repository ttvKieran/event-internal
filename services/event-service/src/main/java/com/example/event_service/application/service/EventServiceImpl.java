package com.example.event_service.application.service;

import com.example.event_service.application.dto.ConfigureEventDTO;
import com.example.event_service.application.dto.CreateEventDTO;
import com.example.event_service.application.dto.EventDetailsDTO;
import com.example.event_service.application.port.in.EventUseCase;
import com.example.event_service.domain.model.aggregate.Event;
import com.example.event_service.domain.model.entity.ResourceAllocation;
import com.example.event_service.domain.model.valueobject.EventSchedule;
import com.example.event_service.domain.model.valueobject.TicketDetail;
import com.example.event_service.domain.model.valueobject.TicketType;
import com.example.event_service.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventUseCase {

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventDetailsDTO createEvent(CreateEventDTO dto) {
        EventSchedule schedule = EventSchedule.of(
            dto.getStartTime(), dto.getEndTime(), null, null
        );

        Event event = Event.createNew(
            dto.getTitle(), dto.getDescription(), dto.getLocation(), schedule
        );

        Event savedEvent = eventRepository.save(event);
        return EventDetailsDTO.fromDomain(event);
    }

    @Override
    @Transactional
    public EventDetailsDTO configureEventDetails(UUID eventId, ConfigureEventDTO dto) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId));

        TicketType ticketType = TicketType.of(dto.getTicketTypeCode());
        TicketDetail ticketDetail = TicketDetail.of(ticketType, dto.getMaxParticipants(), dto.getPrice());

        // Danh sách Tài nguyên
        List<ResourceAllocation> resources = new ArrayList<>();
        if (dto.getResources() != null) {
            for (ConfigureEventDTO.ResourceItem item : dto.getResources()) {
                resources.add(ResourceAllocation.of(
                    UUID.randomUUID(),
                    item.getResourceId(),
                    item.getNote(),
                    java.math.BigDecimal.valueOf(item.getQuantity())
                ));
            }
        }

        event.configureDetails(ticketDetail, resources, dto.getRegistrationOpenAt(), dto.getRegistrationCloseAt());

        eventRepository.save(event);

        return EventDetailsDTO.fromDomain(event);
    }


    @Override
    @Transactional
    public EventDetailsDTO publishEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId));

        event.publish();

        eventRepository.save(event);

        return EventDetailsDTO.fromDomain(event);
    }

    @Override
    @Transactional
    public EventDetailsDTO cancelEvent(UUID eventId, String reason) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId));

         event.cancel();

        eventRepository.save(event);

        return EventDetailsDTO.fromDomain(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId));

        return EventDetailsDTO.fromDomain(event);
    }
}
