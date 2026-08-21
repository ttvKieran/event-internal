package com.example.resource_service.application.port.in;

import com.example.resource_service.application.dto.ConfigureResourceRequest;
import com.example.resource_service.application.dto.CreateResourceRequest;
import com.example.resource_service.domain.model.Resource;

import java.util.List;

public interface ResourceUseCase {
    Resource createResource(CreateResourceRequest cmd);
    List<Resource> listResources(String type);
    Resource getResource(String id);
    Resource configureResource(String id, ConfigureResourceRequest cmd);
}
