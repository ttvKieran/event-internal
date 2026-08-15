package com.example.event_service.application.query.port;

import com.example.event_service.application.dto.query.EventDetailsDTO;
import java.util.UUID;

public interface EventQueryPort {
    EventDetailsDTO fetchEventDetails(UUID eventId);
}
