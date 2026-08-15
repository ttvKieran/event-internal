package com.example.event_service.application.port.in;

import com.example.event_service.application.dto.ConfigureEventDTO;
import com.example.event_service.application.dto.CreateEventDTO;
import com.example.event_service.application.dto.EventDetailsDTO;

import java.util.UUID;

public interface EventUseCase {
    UUID createEvent(CreateEventDTO dto);

    void configureEventDetails(UUID eventId, ConfigureEventDTO dto);

    void publishEvent(UUID eventId);

    void cancelEvent(UUID eventId, String reason);

    EventDetailsDTO getEventDetails(UUID eventId);
}
