package com.example.event_service.application.handler.command;

import com.example.event_service.application.command.CreateEventCommand;
import com.example.event_service.application.dto.command.CreateEventResultDTO;
import com.example.event_service.domain.model.aggregate.Event;
import com.example.event_service.domain.model.valueobject.EventSchedule;
import com.example.event_service.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateEventCommandHandler {

    private final EventRepository eventRepository;

    @Transactional
    public CreateEventResultDTO handle(CreateEventCommand command) {
        EventSchedule schedule = EventSchedule.of(
            command.getStartTime(), command.getEndTime(), null, null
        );

        Event event = Event.createNew(
            command.getTitle(), command.getDescription(), command.getLocation(), schedule
        );

        Event savedEvent = eventRepository.save(event);

        return new CreateEventResultDTO(
            savedEvent.getEventId(),
            savedEvent.getStatus().getCode(),
            savedEvent.getCreatedAt()
        );
    }
}

