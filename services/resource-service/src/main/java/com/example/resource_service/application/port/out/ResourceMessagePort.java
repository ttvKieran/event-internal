package com.example.resource_service.application.port.out;

import com.example.resource_service.application.dto.message.ResourceConfiguredEventPayload;
import com.example.resource_service.application.dto.message.ResourceCreatedEventPayload;

public interface ResourceMessagePort {
    void publishResourceCreated(ResourceCreatedEventPayload payload);
    void publishResourceConfigured(ResourceConfiguredEventPayload payload);
}
