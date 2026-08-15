package com.example.iam_service.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Department {
    private String id;
    private String name;
    private String description;
}
