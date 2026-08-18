package com.example.registration_service.presentation.mapper;

import com.example.registration_service.application.dto.ReserveTicketDTO;
import com.example.registration_service.presentation.dto.request.ReserveTicketRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationApiMapper {

    public ReserveTicketDTO toAppCommand(ReserveTicketRequest request, UUID userId) {
        return ReserveTicketDTO.builder()
            .campaignId(request.getCampaignId())
            .userId(userId)
            .build();
    }
}
