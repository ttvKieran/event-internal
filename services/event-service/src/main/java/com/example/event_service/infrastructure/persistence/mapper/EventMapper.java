package com.example.event_service.infrastructure.persistence.mapper;

import com.example.event_service.domain.model.aggregate.Event;
import com.example.event_service.infrastructure.persistence.entity.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventEntity toEntity(Event domain) {
        if (domain == null) return null;

        EventEntity entity = new EventEntity();
        entity.setId(domain.getEventId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setLocation(domain.getLocation());
        entity.setStatus(domain.getStatus().getCode()); // Vắt mã String ra

        if (domain.getTicketDetails() != null) {
            entity.setTicketTypeCode(domain.getTicketDetails().getType().getCode());
            entity.setPrice(domain.getTicketDetails().getPrice());
            entity.setMaxParticipants(domain.getTicketDetails().getMaxParticipants());
        }

        // (Phần map EventSchedule và Resource Allocation tương tự)
        return entity;
    }

    // CHUYỂN TỪ DB -> LÊN LẠI DOMAIN
    public Event toDomain(EventEntity entity) {
        if (entity == null) return null;

        // LƯU Ý: Chỗ này thường trong class Event bạn sẽ phải viết thêm 1 hàm static là:
        // Event.loadFromDb(...) để nhồi toàn bộ data từ DB khôi phục lại Aggregate.
        // Bạn sẽ tự ráp các Value Object (TicketDetails, EventSchedule) rồi ném vào hàm đó nhé!
        return null; // Tôi để tạm null để không báo lỗi biên dịch
    }
}
