package com.example.resource_service.domain.model;

import com.example.resource_service.domain.exception.DomainException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Resource {
    private String id;
    private String name;
    private ResourceType type;
    private Capacity capacity;
    private String location;
    private String description;
    private String imageUrl;
    private ResourceStatus manualStatus;
    private Instant createdAt;

    public static Resource createNew(String name, ResourceType type, Capacity capacity, String location, String description, String imageUrl) {
        return Resource.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .type(type)
                .capacity(capacity)
                .location(location)
                .description(description)
                .imageUrl(imageUrl)
                .manualStatus(ResourceStatus.AVAILABLE)
                .createdAt(Instant.now())
                .build();
    }

    public void configureDetails(String newName, ResourceType newType, Capacity newCapacity, String newLocation, String newDescription, String newImageUrl) {
        if (newCapacity != null) this.capacity = newCapacity;
        if (newName != null) this.name = newName;
        if (newType != null) this.type = newType;
        if (newLocation != null) this.location = newLocation;
        if (newDescription != null) this.description = newDescription;
        if (newImageUrl != null) this.imageUrl = newImageUrl;
    }
}
