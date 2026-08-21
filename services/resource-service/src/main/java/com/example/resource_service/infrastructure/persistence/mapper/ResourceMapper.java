package com.example.resource_service.infrastructure.persistence.mapper;

import com.example.resource_service.domain.model.*;
import com.example.resource_service.infrastructure.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public Resource toDomain(ResourceJpaEntity entity) {
        if (entity == null) return null;
        
        return Resource.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .name(entity.getName())
                .type(entity.getType() != null ? ResourceType.valueOf(entity.getType()) : null)
                .capacity(entity.getCapacity() != null ? new Capacity(entity.getCapacity().getValue(), entity.getCapacity().getUnit()) : null)
                .location(entity.getLocation())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .manualStatus(entity.getManualStatus() != null ? ResourceStatus.valueOf(entity.getManualStatus()) : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ResourceJpaEntity toEntity(Resource domain) {
        if (domain == null) return null;
        
        ResourceJpaEntity entity = new ResourceJpaEntity();
        entity.setId(domain.getId() != null ? java.util.UUID.fromString(domain.getId()) : null);
        entity.setName(domain.getName());
        entity.setType(domain.getType() != null ? domain.getType().name() : null);
        
        if (domain.getCapacity() != null) {
            entity.setCapacity(new CapacityJpaEmbeddable(
                    domain.getCapacity().getValue(),
                    domain.getCapacity().getUnit()
            ));
        }
        
        entity.setLocation(domain.getLocation());
        entity.setDescription(domain.getDescription());
        entity.setImageUrl(domain.getImageUrl());
        entity.setManualStatus(domain.getManualStatus() != null ? domain.getManualStatus().name() : null);
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }
}
