package com.example.resource_service.application.service;

import com.example.resource_service.application.dto.ConfigureResourceRequest;
import com.example.resource_service.application.dto.CreateResourceRequest;
import com.example.resource_service.application.dto.message.ResourceConfiguredEventPayload;
import com.example.resource_service.application.dto.message.ResourceCreatedEventPayload;
import com.example.resource_service.application.port.in.ResourceUseCase;
import com.example.resource_service.application.port.out.ResourceMessagePort;
import com.example.resource_service.domain.exception.DomainException;
import com.example.resource_service.domain.model.Resource;
import com.example.resource_service.domain.repository.IResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceApplicationService implements ResourceUseCase {
    private final IResourceRepository repository;
    private final ResourceMessagePort messagePort;

    @Override
    @Transactional
    public Resource createResource(CreateResourceRequest cmd) {
        Resource resource = Resource.createNew(
                cmd.getName(), cmd.getType(), cmd.getCapacity(),
                cmd.getLocation(), cmd.getDescription(), cmd.getImageUrl()
        );

        Resource saved = repository.save(resource);

        messagePort.publishResourceCreated(ResourceCreatedEventPayload.builder()
            .resourceId(saved.getId())
            .name(saved.getName())
            .type(saved.getType())
            .capacity(saved.getCapacity())
            .location(saved.getLocation())
            .createdAt(saved.getCreatedAt())
            .build());

        return saved;
    }

    @Override
    public List<Resource> listResources(String type) {
        if (type != null && !type.isEmpty()) {
            return repository.findByType(type);
        }
        return repository.findAll();
    }

    @Override
    public Resource getResource(String id) {
        return repository.findById(id).orElseThrow(() -> new DomainException("Resource not found"));
    }

    @Override
    @Transactional
    public Resource configureResource(String id, ConfigureResourceRequest cmd) {
        Resource resource = getResource(id);

        resource.configureDetails(
                cmd.getName(),
                cmd.getType(),
                cmd.getCapacity(),
                cmd.getLocation(),
                cmd.getDescription(),
                cmd.getImageUrl()
        );

        Resource saved = repository.save(resource);

        messagePort.publishResourceConfigured(ResourceConfiguredEventPayload.builder()
            .resourceId(saved.getId())
            .type(saved.getType())
            .capacity(saved.getCapacity())
            .location(saved.getLocation())
            .configuredAt(Instant.now())
            .build());

        return saved;
    }
}
