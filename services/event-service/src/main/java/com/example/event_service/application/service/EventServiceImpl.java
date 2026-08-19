package com.example.event_service.application.service;

import com.example.event_service.application.dto.ConfigureEventDTO;
import com.example.event_service.application.dto.CreateEventDTO;
import com.example.event_service.application.dto.EventDetailsDTO;
import com.example.event_service.application.dto.message.EventCancelledEventPayload;
import com.example.event_service.application.dto.message.EventCreatedEventPayload;
import com.example.event_service.application.dto.message.EventDetailsConfiguredEventPayload;
import com.example.event_service.application.dto.message.EventPublishedEventPayload;
import com.example.event_service.application.port.in.EventUseCase;
import com.example.event_service.application.port.out.EventMessagePort;
import com.example.event_service.domain.model.aggregate.Event;
import com.example.event_service.domain.model.entity.ResourceAllocation;
import com.example.event_service.domain.model.valueobject.EventSchedule;
import com.example.event_service.domain.model.valueobject.EventStatus;
import com.example.event_service.domain.model.valueobject.TicketDetail;
import com.example.event_service.domain.model.valueobject.TicketType;
import com.example.event_service.domain.repository.EventRepository;
import com.example.event_service.infrastructure.client.RegistrationClient;
import com.example.event_service.infrastructure.client.dto.CampaignStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventUseCase {

    private final EventRepository eventRepository;
    private final EventMessagePort eventMessagePort;
    private final RegistrationClient registrationClient;

    @Override
    @Transactional
    public EventDetailsDTO createEvent(CreateEventDTO dto) {
        EventSchedule schedule = EventSchedule.of(
            dto.getStartTime(), dto.getEndTime(), null, null
        );

        Event event = Event.createNew(
            dto.getTitle(), dto.getDescription(), dto.getLocation(), schedule
        );

        Event savedEvent = eventRepository.save(event);

        String correlationId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
        eventMessagePort.sendEventCreatedEvent(
            EventCreatedEventPayload.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .createdAt(LocalDateTime.now())
                .build(),
            correlationId
        );

        return EventDetailsDTO.fromDomain(event);
    }

    @Override
    @Transactional
    public EventDetailsDTO configureEventDetails(UUID eventId, ConfigureEventDTO dto) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId));

        TicketType ticketType = TicketType.of(dto.getTicketTypeCode());
        TicketDetail ticketDetail = TicketDetail.of(ticketType, dto.getMaxParticipants(), dto.getPrice());

        // Danh sách Tài nguyên
        List<ResourceAllocation> resources = new ArrayList<>();
        if (dto.getResources() != null) {
            for (ConfigureEventDTO.ResourceItem item : dto.getResources()) {
                resources.add(ResourceAllocation.of(
                    UUID.randomUUID(),
                    item.getResourceId(),
                    item.getNote(),
                    java.math.BigDecimal.valueOf(item.getQuantity())
                ));
            }
        }

        // Gọi HTTP sang Resource Service để check xem phòng có còn trống trong khung giờ của sự kiện hay không.

        event.configureDetails(ticketDetail, resources, dto.getRegistrationOpenAt(), dto.getRegistrationCloseAt());

        eventRepository.save(event);

        // Map danh sách tài nguyên sang DTO của Message
        String correlationId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
        List<EventDetailsConfiguredEventPayload.ResourceAllocationMessage> resourceMessages = new ArrayList<>();
        if (event.getAllocatedResources() != null) {
            event.getAllocatedResources().forEach(res -> {
                resourceMessages.add(new EventDetailsConfiguredEventPayload.ResourceAllocationMessage(
                    res.getResourceId(),
                    res.getQuantity().doubleValue()
                ));
            });
        }

        eventMessagePort.sendEventDetailsConfiguredEvent(
            EventDetailsConfiguredEventPayload.builder()
                .eventId(event.getEventId())
                .ticketType(event.getTicketDetails().getType().getCode())
                .allocatedResources(resourceMessages)
                .maxParticipants(event.getTicketDetails().getMaxParticipants())
                .registrationOpenAt(event.getSchedule().getRegistrationOpenAt())
                .registrationCloseAt(event.getSchedule().getRegistrationCloseAt())
                .price(event.getTicketDetails().getPrice())
                .build(),
            correlationId
        );

        return EventDetailsDTO.fromDomain(event);
    }

    @Override
    @Transactional
    public EventDetailsDTO publishEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId));

        event.publish();

        eventRepository.save(event);

        // Tạm thời để ngẫu nhiên
        String correlationId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);

        eventMessagePort.sendEventPublishedEvent(
            new EventPublishedEventPayload(event.getEventId(), LocalDateTime.now()),
            correlationId
        );

        return EventDetailsDTO.fromDomain(event);
    }

    @Override
    @Transactional(noRollbackFor = IllegalStateException.class)
    public EventDetailsDTO cancelEvent(UUID eventId, String reason) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId));

        // Gọi RPI sang Registration service
        CampaignStatsResponse stats = registrationClient.getStats(eventId.toString());
        if (stats.getPaidCount() == -1) {
            throw new IllegalStateException("Hệ thống kiểm tra vé đang bảo trì, không thể hủy sự kiện lúc này!");
        }
        if (stats.getPaidCount() > 0) {
            event.pendingCancellation();
            eventRepository.save(event);
            throw new IllegalStateException("Sự kiện đã bán vé! Đã chuyển sang trạng thái chờ xử lý hoàn tiền.");
        }

        event.cancel();
        eventRepository.save(event);

        String correlationId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
        eventMessagePort.sendEventCancelledEvent(
            EventCancelledEventPayload.builder()
                .eventId(event.getEventId())
                .reason(reason)
                .cancelledAt(LocalDateTime.now())
                .build(),
            correlationId
        );

        return EventDetailsDTO.fromDomain(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId));

        return EventDetailsDTO.fromDomain(event);
    }
}
