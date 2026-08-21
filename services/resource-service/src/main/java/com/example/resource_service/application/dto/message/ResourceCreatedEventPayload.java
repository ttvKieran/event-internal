package com.example.resource_service.application.dto.message;

import com.example.resource_service.domain.model.Capacity;
import com.example.resource_service.domain.model.ResourceType;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ResourceCreatedEventPayload {
    private String resourceId;
    private String name;
    private ResourceType type;
    private Capacity capacity;
    private String location;
    private Instant createdAt;
}
