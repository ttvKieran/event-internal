package com.example.registration_service.infrastructure.messaging.kafka.exception;

public final class KafkaMessageProcessingException extends RuntimeException {

    public KafkaMessageProcessingException(
        String messageId,
        Throwable cause) {
        super("Failed to process Kafka message: " + messageId, cause);
    }
}
