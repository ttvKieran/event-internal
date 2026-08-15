package com.example.event_service.infrastructure.persistence.adapter;

import com.example.event_service.application.dto.query.EventDetailsDTO;
import com.example.event_service.application.query.port.EventQueryPort;
import com.example.event_service.infrastructure.persistence.repository.EventQueryDao;
import com.example.event_service.infrastructure.persistence.repository.EventSummaryView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EventQueryPortImpl implements EventQueryPort {

    private final EventQueryDao eventQueryDao;

    @Override
    public EventDetailsDTO fetchEventDetails(UUID eventId) {
        Optional<EventSummaryView> viewOpt = eventQueryDao.fetchEventDetails(eventId);

        if (viewOpt.isEmpty()) {
            return null;
        }

        EventSummaryView view = viewOpt.get();

        return new EventDetailsDTO(
            view.getId(),
            view.getTitle(),
            view.getStatus(),
            view.getTicketTypeCode(),
            view.getPrice(),
            view.getStartTime()
        );
    }
}
