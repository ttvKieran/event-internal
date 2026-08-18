package com.example.iam_service.infrastructure.persistence.adapter;

import com.example.iam_service.domain.model.OutboxEvent;
import com.example.iam_service.domain.repository.IOutboxEventRepository;
import com.example.iam_service.infrastructure.persistence.entity.OutboxEventJpaEntity;
import com.example.iam_service.infrastructure.persistence.mapper.OutboxEventMapper;
import com.example.iam_service.infrastructure.persistence.repository.OutboxEventSpringDataRepository;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventRepositoryImpl implements IOutboxEventRepository {

    private final OutboxEventSpringDataRepository repository;

    public OutboxEventRepositoryImpl(OutboxEventSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        OutboxEventJpaEntity entity = OutboxEventMapper.toJpaEntity(event);
        OutboxEventJpaEntity saved = repository.save(entity);
        return OutboxEventMapper.toDomainModel(saved);
    }
}
