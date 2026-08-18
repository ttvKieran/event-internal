package com.example.iam_service.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OutboxEvent {
    private String id;
    private String aggregateType;
    private String aggregateId;
    private String type;
    private String payload;
    private LocalDateTime createdAt;
}
