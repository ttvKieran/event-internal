package com.example.event_service.application.port.out;

import com.example.event_service.application.dto.message.EventCancelledEventPayload;
import com.example.event_service.application.dto.message.EventCreatedEventPayload;
import com.example.event_service.application.dto.message.EventDetailsConfiguredEventPayload;
import com.example.event_service.application.dto.message.EventPublishedEventPayload;

public interface EventMessagePort {
    void sendEventCreatedEvent(EventCreatedEventPayload payload, String correlationId);
    void sendEventDetailsConfiguredEvent(EventDetailsConfiguredEventPayload payload, String correlationId);
    void sendEventPublishedEvent(EventPublishedEventPayload payload, String correlationId);
    void sendEventCancelledEvent(EventCancelledEventPayload payload, String correlationId);
}
