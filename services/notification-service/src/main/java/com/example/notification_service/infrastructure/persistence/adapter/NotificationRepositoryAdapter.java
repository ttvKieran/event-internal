package com.example.notification_service.infrastructure.persistence.adapter;

import com.example.notification_service.domain.model.aggregate.Notification;
import com.example.notification_service.domain.model.valueobject.NotificationStatus;
import com.example.notification_service.domain.repository.NotificationRepository;
import com.example.notification_service.infrastructure.persistence.mapper.NotificationMapper;
import com.example.notification_service.infrastructure.persistence.repository.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final JpaNotificationRepository jpaRepo;

    @Override
    public Notification save(Notification notification) {
        return NotificationMapper.toDomain(
            jpaRepo.save(NotificationMapper.toEntity(notification))
        );
    }

    @Override
    public List<Notification> findByStatus(NotificationStatus status) {
        return jpaRepo.findByStatus(status.getCode())
            .stream()
            .map(NotificationMapper::toDomain)
            .toList();
    }
}
