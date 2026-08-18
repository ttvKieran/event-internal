package com.example.iam_service.domain.repository;

import com.example.iam_service.domain.model.OutboxEvent;

public interface IOutboxEventRepository {
    OutboxEvent save(OutboxEvent event);
}
