package com.example.resource_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resources")
@Getter @Setter
public class ResourceJpaEntity {
    @Id
    private UUID id;
    
    private String name;
    private String type;
    
    @Embedded
    private CapacityJpaEmbeddable capacity;
    
    private String location;
    private String description;
    private String imageUrl;
    private String manualStatus;
    private Instant createdAt;
}
