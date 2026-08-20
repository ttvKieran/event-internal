package com.example.resource_service.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapacityJpaEmbeddable {

    @Column(name = "capacity_value")
    private double value;

    @Column(name = "capacity_unit")
    private String unit;
}
