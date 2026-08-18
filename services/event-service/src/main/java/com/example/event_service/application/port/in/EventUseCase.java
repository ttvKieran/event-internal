package com.example.event_service.application.port.in;

import com.example.event_service.application.dto.ConfigureEventDTO;
import com.example.event_service.application.dto.CreateEventDTO;
import com.example.event_service.application.dto.EventDetailsDTO;

import java.util.UUID;

public interface EventUseCase {
    EventDetailsDTO createEvent(CreateEventDTO dto);

    EventDetailsDTO configureEventDetails(UUID eventId, ConfigureEventDTO dto);

    EventDetailsDTO publishEvent(UUID eventId);

    EventDetailsDTO cancelEvent(UUID eventId, String reason);

    EventDetailsDTO getEventDetails(UUID eventId);
}
