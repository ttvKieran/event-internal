package com.example.registration_service.presentation.mapper;

import com.example.registration_service.application.model.command.ReserveTicketCommand;
import com.example.registration_service.application.model.result.RegistrationResult;
import com.example.registration_service.presentation.dto.request.ReserveTicketRequest;
import com.example.registration_service.presentation.dto.response.RegistrationResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationApiMapper {

    public ReserveTicketCommand toAppCommand(ReserveTicketRequest request, UUID userId) {
        return new ReserveTicketCommand(request.getCampaignId(), userId, request.getProvider());
    }

    public RegistrationResponse toResponse(RegistrationResult result) {
        return new RegistrationResponse(
            result.registrationId(),
            result.campaignId(),
            result.userId(),
            result.status(),
            result.cancelReason(),
            result.registeredAt()
        );
    }
}
