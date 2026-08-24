package com.example.registration_service.infrastructure.messaging.kafka.adapter;

import com.example.registration_service.application.model.command.ReserveTicketCommand;
import com.example.registration_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.example.registration_service.infrastructure.persistence.repository.JpaOutboxRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrationMessageAdapterTest {

    @Mock
    private JpaOutboxRepository outboxRepository;

    @Test
    void asyncCommandKeepsExistingKafkaPayloadShape()
        throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RegistrationMessageAdapter adapter =
            new RegistrationMessageAdapter(
                outboxRepository,
                objectMapper
            );
        UUID campaignId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        adapter.publishAsyncReserveTicketCommand(
            new ReserveTicketCommand(
                campaignId,
                userId,
                "vnpay"
            )
        );

        ArgumentCaptor<OutboxEventEntity> captor =
            ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(outboxRepository).save(captor.capture());

        JsonNode payload = objectMapper.readTree(
            captor.getValue().getPayload()
        );
        assertEquals(
            campaignId.toString(),
            payload.get("campaignId").asText()
        );
        assertEquals("VNPAY", payload.at("/provider/code").asText());
        assertFalse(payload.has("providerCode"));
    }
}
