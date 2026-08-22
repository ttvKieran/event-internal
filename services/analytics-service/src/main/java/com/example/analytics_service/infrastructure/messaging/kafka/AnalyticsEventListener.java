package com.example.analytics_service.infrastructure.messaging.kafka;

import com.example.analytics_service.application.port.in.UpdateMetricsUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventListener {
    private final UpdateMetricsUseCase updateMetricsUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "Event", groupId = "analytics-group")
    public void onEventServiceEvent(String messagePayload) {
        try {
            JsonNode root = objectMapper.readTree(messagePayload);
            String type = extractType(root);
            JsonNode data = extractPayload(root);
            
            if ("EventCreatedEvent".equals(type)) {
                updateMetricsUseCase.processEventCreated(UUID.fromString(data.get("eventId").asText()));
            } 
            else if ("EventDetailsConfiguredEvent".equals(type)) {
                UUID eventId = UUID.fromString(data.get("eventId").asText());
                String ticketType = data.get("ticketType").asText(); 
                int maxParticipants = data.has("maxParticipants") && !data.get("maxParticipants").isNull() 
                        ? data.get("maxParticipants").asInt() : 0;
                BigDecimal price = data.has("price") && !data.get("price").isNull() 
                        ? new BigDecimal(data.get("price").asText()) : BigDecimal.ZERO;
                
                updateMetricsUseCase.processEventDetailsConfigured(eventId, ticketType, maxParticipants, price);
            }
        } catch (Exception e) {
            log.error("Error Kafka Event handle (Event Topic): {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "Registration", groupId = "analytics-group")
    public void onRegistrationEvent(String messagePayload) {
        try {
            JsonNode root = objectMapper.readTree(messagePayload);
            String type = extractType(root);
            JsonNode data = extractPayload(root);
            UUID eventId = UUID.fromString(data.get("campaignId").asText());

            if ("RegistrationConfirmedEvent".equals(type)) {
                updateMetricsUseCase.processRegistrationConfirmed(eventId);
            } else if ("PaidRegistrationRolledBackEvent".equals(type)) {
                updateMetricsUseCase.processRegistrationRolledBack(eventId);
            }
        } catch (Exception e) {
            log.error("Error Kafka Event handle (Registration Topic): {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "Attendance", groupId = "analytics-group")
    public void onAttendanceEvent(String messagePayload) {
        try {
            JsonNode root = objectMapper.readTree(messagePayload);
            String type = extractType(root);
            JsonNode data = extractPayload(root);
            
            if ("ParticipantCheckedInEvent".equals(type)) {
                updateMetricsUseCase.processParticipantCheckedIn(UUID.fromString(data.get("eventId").asText()));
            }
        } catch (Exception e) {
            log.error("Error Kafka Event handle (Attendance Topic): {}", e.getMessage());
        }
    }

    private String extractType(JsonNode root) {
        String t = root.path("payload").path("type").asText();
        return (t == null || t.isEmpty()) ? root.path("type").asText() : t;
    }

    private JsonNode extractPayload(JsonNode root) throws Exception {
        String pStr = root.path("payload").path("payload").asText();
        if (pStr == null || pStr.isEmpty()) pStr = root.path("payload").asText();
        return objectMapper.readTree(pStr);
    }
}
