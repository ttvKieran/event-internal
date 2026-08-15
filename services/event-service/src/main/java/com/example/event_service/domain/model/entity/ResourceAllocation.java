package com.example.event_service.domain.model.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceAllocation {
    private UUID id;
    private UUID resourceId;
    private String note;
    private BigDecimal quantity;

    public static ResourceAllocation of(UUID id, UUID resourceId, String note, BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số lượng tài nguyên phải > 0");
        }
        return new ResourceAllocation(id, resourceId, note, quantity);
    }
}
