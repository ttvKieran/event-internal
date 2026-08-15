package com.example.event_service.domain.model.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketDetail {
    TicketType type;
    Integer maxParticipants;
    BigDecimal price;

    public static TicketDetail of(TicketType type, Integer maxParticipants, BigDecimal price) {
        if (maxParticipants == null || maxParticipants <= 0) {
            throw new IllegalArgumentException("Số người tham gia tối đa phải > 0");
        }
        // Kiểm tra vé FREE: Price không được lớn hơn 0
        if (type.equals(TicketType.FREE) && price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Vé FREE không được phép có giá tiền");
        }
        // Kiểm tra vé PAID: Price bắt buộc phải lớn hơn 0
        if (type.equals(TicketType.PAID) && (price == null || price.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException("Vé PAID bắt buộc phải có giá > 0");
        }
        return new TicketDetail(type, maxParticipants, price);
    }
}
