package com.example.event_service.infrastructure.persistence.mapper;

import com.example.event_service.domain.model.aggregate.Event;
import com.example.event_service.domain.model.entity.ResourceAllocation;
import com.example.event_service.domain.model.valueobject.EventSchedule;
import com.example.event_service.domain.model.valueobject.EventStatus;
import com.example.event_service.domain.model.valueobject.TicketDetail;
import com.example.event_service.domain.model.valueobject.TicketType;
import com.example.event_service.infrastructure.persistence.entity.EventEntity;
import com.example.event_service.infrastructure.persistence.entity.ResourceAllocationEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    // GHI XUỐNG DB
    public EventEntity toEntity(Event domain) {
        if (domain == null) return null;

        EventEntity entity = new EventEntity();
        entity.setId(domain.getEventId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setLocation(domain.getLocation());
        entity.setStatus(domain.getStatus().getCode());
        entity.setCreatedAt(domain.getCreatedAt());

        if (domain.getTicketDetails() != null) {
            entity.setTicketTypeCode(domain.getTicketDetails().getType().getCode());
            entity.setPrice(domain.getTicketDetails().getPrice());
            entity.setMaxParticipants(domain.getTicketDetails().getMaxParticipants());
        }

        if (domain.getSchedule() != null) {
            entity.setStartTime(domain.getSchedule().getStartTime());
            entity.setEndTime(domain.getSchedule().getEndTime());
            entity.setRegistrationStartTime(domain.getSchedule().getRegistrationOpenAt());
            entity.setRegistrationEndTime(domain.getSchedule().getRegistrationCloseAt());
        }

        if (domain.getAllocatedResources() != null) {
            List<ResourceAllocationEntity> resourceEntities = domain.getAllocatedResources().stream()
                .map(res -> {
                    ResourceAllocationEntity resEntity = new ResourceAllocationEntity();
                    resEntity.setId(res.getId());
                    resEntity.setResourceId(res.getResourceId());
                    resEntity.setNote(res.getNote());
                    resEntity.setQuantity(res.getQuantity());
                    resEntity.setEvent(entity);
                    return resEntity;
                }).collect(Collectors.toList());
            entity.setResources(resourceEntities);
        }

        return entity;
    }

    // MÓC TỪ DB LÊN
    public Event toDomain(EventEntity entity) {
        if (entity == null) return null;

        EventSchedule schedule = null;
        if (entity.getStartTime() != null) {
            schedule = EventSchedule.of(
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getRegistrationStartTime(),
                entity.getRegistrationEndTime()
            );
        }

        TicketDetail ticketDetail = null;
        if (entity.getTicketTypeCode() != null) {
            ticketDetail = TicketDetail.of(
                TicketType.of(entity.getTicketTypeCode()),
                entity.getMaxParticipants(),
                entity.getPrice()
            );
        }

        List<ResourceAllocation> resources = new ArrayList<>();
        if (entity.getResources() != null) {
            resources = entity.getResources().stream()
                .map(resEntity -> ResourceAllocation.of(
                    resEntity.getId(),
                    resEntity.getResourceId(),
                    resEntity.getNote(),
                    resEntity.getQuantity()
                )).collect(Collectors.toList());
        }

        return Event.reconstitute(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getLocation(),
            EventStatus.of(entity.getStatus()),
            schedule,
            ticketDetail,
            resources,
            entity.getCreatedAt()
        );
    }
}
