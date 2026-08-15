package com.example.event_service.application.handler.command;

import com.example.event_service.application.command.ConfigureEventCommand;
import com.example.event_service.domain.model.aggregate.Event;
import com.example.event_service.domain.model.entity.ResourceAllocation;
import com.example.event_service.domain.model.valueobject.TicketDetail;
import com.example.event_service.domain.model.valueobject.TicketDetail;
import com.example.event_service.domain.model.valueobject.TicketType;
import com.example.event_service.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigureEventCommandHandler {

    private final EventRepository eventRepository;

    @Transactional
    public void handle(ConfigureEventCommand command) {
        // Lấy Event từ DB lên
        Event event = eventRepository.findById(command.getEventId())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Event ID: " + command.getEventId()));

        // Tạo Value Object cho Ticket
        TicketType type = TicketType.of(command.getTicketTypeCode());
        TicketDetail ticket = TicketDetail.of(type, command.getMaxParticipants(), command.getPrice());

        // Tạo danh sách tài nguyên
        List<ResourceAllocation> resources = command.getResources().stream()
            .map(req -> ResourceAllocation.of(UUID.randomUUID(), UUID.fromString(req.getResourceId()), req.getNote(), req.getQuantity()))
            .collect(Collectors.toList());

        event.configureDetails(ticket, resources);

        eventRepository.save(event);
    }
}

