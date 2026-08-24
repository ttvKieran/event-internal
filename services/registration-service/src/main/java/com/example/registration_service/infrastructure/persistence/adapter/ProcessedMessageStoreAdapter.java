package com.example.registration_service.infrastructure.persistence.adapter;

import com.example.registration_service.application.port.out.ProcessedMessageStore;
import com.example.registration_service.infrastructure.persistence.repository.JpaProcessedMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProcessedMessageStoreAdapter implements ProcessedMessageStore {

    private final JpaProcessedMessageRepository repository;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean tryClaim(String messageId, Instant processedAt) {
        return repository.insertIfAbsent(messageId, processedAt) == 1;
    }
}
