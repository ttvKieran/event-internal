package com.example.registration_service.application.port.out;

import java.time.Instant;

public interface ProcessedMessageStore {

    boolean tryClaim(String messageId, Instant processedAt);
}
