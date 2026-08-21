package com.example.resource_service.application.dto;

import com.example.resource_service.domain.model.Capacity;
import com.example.resource_service.domain.model.ResourceType;
import lombok.Data;

@Data
public class ConfigureResourceRequest {
    private String name;
    private ResourceType type;
    private Capacity capacity;
    private String location;
    private String description;
    private String imageUrl;
}
