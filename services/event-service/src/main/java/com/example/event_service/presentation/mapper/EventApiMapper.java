package com.example.event_service.presentation.mapper;

import com.example.event_service.application.dto.ConfigureEventDTO;
import com.example.event_service.application.dto.CreateEventDTO;
import com.example.event_service.application.dto.EventDetailsDTO;
import com.example.event_service.presentation.dto.request.ConfigureEventDetailsRequestDTO;
import com.example.event_service.presentation.dto.request.CreateEventRequestDTO;
import com.example.event_service.presentation.dto.response.EventResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventApiMapper {

    CreateEventDTO toAppDto(CreateEventRequestDTO request);

    EventResponseDTO toResponseDto(EventDetailsDTO appResult);

    ConfigureEventDTO toAppDto(ConfigureEventDetailsRequestDTO request);
}
