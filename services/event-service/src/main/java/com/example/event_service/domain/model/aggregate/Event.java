package com.example.event_service.domain.model.aggregate;

import com.example.event_service.domain.model.entity.ResourceAllocation;
import com.example.event_service.domain.model.valueobject.EventSchedule;
import com.example.event_service.domain.model.valueobject.EventStatus;
import com.example.event_service.domain.model.valueobject.TicketDetail;
import com.example.event_service.domain.model.valueobject.TicketDetail;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@EqualsAndHashCode(of = "eventId")
public class Event {
    private UUID eventId;
    private String title;
    private String description;
    private String location;
    private LocalDateTime createdAt;

    private EventStatus status;
    private EventSchedule schedule;
    private TicketDetail ticketDetails;
    private List<ResourceAllocation> allocatedResources;
    // Phải để constructor private để cấm khởi tạo tự do bằng từ khóa `new`
    private Event() {}
    public static Event createNew(String title, String description, String location, EventSchedule schedule) {
        Event event = new Event();
        event.eventId = UUID.randomUUID();
        event.title = title;
        event.description = description;
        event.location = location;
        event.schedule = schedule;
        event.status = EventStatus.DRAFT;
        event.createdAt = LocalDateTime.now();
        event.allocatedResources = new ArrayList<>();
        return event;
    }
    public void configureDetails(TicketDetail ticketDetails, List<ResourceAllocation> resources) {
        if (!this.status.canTransitionTo(EventStatus.CONFIGURED)) {
            throw new IllegalStateException("Không thể cấu hình ở trạng thái hiện tại: " + this.status.getCode());
        }
        this.ticketDetails = ticketDetails;
        this.allocatedResources = resources != null ? resources : new ArrayList<>();
        this.status = EventStatus.CONFIGURED;
    }
    public void publish() {
        if (!this.status.canTransitionTo(EventStatus.PUBLISHED)) {
            throw new IllegalStateException("Sự kiện chưa đủ điều kiện để công bố");
        }
        this.status = EventStatus.PUBLISHED;
    }
    public void cancel() {
        if (!this.status.canTransitionTo(EventStatus.CANCELLED)) {
            throw new IllegalStateException("Không thể hủy sự kiện lúc này");
        }
        this.status = EventStatus.CANCELLED;
    }
}
