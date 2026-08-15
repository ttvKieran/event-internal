package com.example.event_service.presentation.rest;

import com.example.event_service.application.command.ConfigureEventCommand;
import com.example.event_service.application.command.CreateEventCommand;
import com.example.event_service.application.dto.command.CreateEventResultDTO;
import com.example.event_service.application.handler.command.ConfigureEventCommandHandler;
import com.example.event_service.application.handler.command.CreateEventCommandHandler;
import com.example.event_service.presentation.dto.request.ConfigureEventDetailsRequestDTO;
import com.example.event_service.presentation.dto.request.CreateEventRequestDTO;
import com.example.event_service.presentation.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventCommandController {

    // 1. Inject trực tiếp các Handler thay vì Port
    private final CreateEventCommandHandler createEventCommandHandler;
    private final ConfigureEventCommandHandler configureEventCommandHandler;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateEventResultDTO>> createEvent(@RequestBody CreateEventRequestDTO dto) {

        // Map DTO sang Command
        CreateEventCommand command = new CreateEventCommand(
            dto.getTitle(),
            dto.getDescription(),
            dto.getLocation(),
            dto.getStartTime(),
            dto.getEndTime()
        );

        // Gọi thẳng Handler
        CreateEventResultDTO result = createEventCommandHandler.handle(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @PutMapping("/{eventId}/details")
    public ResponseEntity<ApiResponse<Void>> configureEventDetails(
        @PathVariable UUID eventId,
        @RequestBody ConfigureEventDetailsRequestDTO dto) {

        // Map List<DTO> sang List<Command.ResourceItem>
        var resources = dto.getAllocatedResources().stream()
            .map(res -> new ConfigureEventCommand.ResourceItem(
                res.getResourceId().toString(),
                res.getNote(),
                java.math.BigDecimal.valueOf(res.getQuantity())))
            .collect(Collectors.toList());

        // Map DTO sang Command
        ConfigureEventCommand command = new ConfigureEventCommand(
            eventId,
            dto.getTicketType(),
            dto.getMaxParticipants(),
            dto.getPrice(),
            resources
        );

        // Gọi thẳng Handler
        configureEventCommandHandler.handle(command);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
