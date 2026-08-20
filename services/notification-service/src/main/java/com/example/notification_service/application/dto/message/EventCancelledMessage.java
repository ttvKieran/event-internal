package com.example.notification_service.application.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventCancelledMessage {
    private UUID eventId;
    private String reason;
    private String cancelledAt;
}
