package com.example.event_service.application.query;

import lombok.Value;
import java.util.UUID;

@Value
public class GetEventDetailsQuery {
    UUID eventId;
}
