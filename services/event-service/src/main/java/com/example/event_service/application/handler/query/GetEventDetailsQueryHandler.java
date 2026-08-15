package com.example.event_service.application.handler.query;

import com.example.event_service.application.dto.query.EventDetailsDTO;
import com.example.event_service.application.query.GetEventDetailsQuery;
import com.example.event_service.application.query.port.EventQueryPort; // Gọi đúng Port của nhà mình
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetEventDetailsQueryHandler {

    private final EventQueryPort eventQueryPort;

    public EventDetailsDTO handle(GetEventDetailsQuery query) {
        return eventQueryPort.fetchEventDetails(query.getEventId());
    }
}
