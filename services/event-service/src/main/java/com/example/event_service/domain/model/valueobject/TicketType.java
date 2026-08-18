package com.example.event_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketType {
    public static final TicketType FREE = new TicketType("FREE");
    public static final TicketType PAID = new TicketType("PAID");

    private final String code;

    public static TicketType of(String code) {
        if (code == null) throw new IllegalArgumentException("Loại vé không được trống");
        return switch (code.toUpperCase()) {
            case "FREE" -> FREE;
            case "PAID" -> PAID;
            default -> throw new IllegalArgumentException("Loại vé không hợp lệ: " + code);
        };
    }
}


