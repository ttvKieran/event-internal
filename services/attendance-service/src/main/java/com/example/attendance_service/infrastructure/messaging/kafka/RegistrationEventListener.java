package com.example.attendance_service.infrastructure.messaging.kafka;
import com.example.attendance_service.infrastructure.persistence.entity.RegistrationReadModelEntity;
import com.example.attendance_service.infrastructure.persistence.repository.RegistrationReadModelSpringRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class RegistrationEventListener {
    private final RegistrationReadModelSpringRepository repo;
    private final ObjectMapper objectMapper;
    public RegistrationEventListener(RegistrationReadModelSpringRepository repo, ObjectMapper objectMapper) {
        this.repo = repo;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "Registration", groupId = "attendance-registration-group")
    public void consumeRegistrationEvents(String messagePayload) {
        try {
            JsonNode root = objectMapper.readTree(messagePayload);
            String type = root.path("payload").path("type").asText();
            if (type == null || type.isEmpty()) type = root.path("type").asText();
            String innerPayloadStr = root.path("payload").path("payload").asText();
            if (innerPayloadStr == null || innerPayloadStr.isEmpty()) innerPayloadStr = root.path("payload").asText();
            JsonNode payloadObj = objectMapper.readTree(innerPayloadStr);
            if ("RegistrationConfirmedEvent".equals(type) || "RegistrationCancelledEvent".equals(type)) {
                RegistrationReadModelEntity entity = new RegistrationReadModelEntity();
                entity.setRegistrationId(UUID.fromString(payloadObj.get("registrationId").asText()));
                entity.setEventId(UUID.fromString(payloadObj.get("eventId").asText()));
                entity.setEmployeeId(UUID.fromString(payloadObj.get("employeeId").asText()));
                if ("RegistrationConfirmedEvent".equals(type)) {
                    entity.setStatus("CONFIRMED");
                } else {
                    entity.setStatus("CANCELLED");
                }
                repo.save(entity);
            }
        } catch (Exception e) {
            System.err.println("Error Kafka Registration Event: " + e.getMessage());
        }
    }
}
