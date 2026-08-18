package com.example.event_service.presentation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class EventResourceAllocationDTO {
    private UUID id;
    private String note;
    private UUID resourceId;
    private BigDecimal quantity;
}
