package com.example.resource_service.presentation.rest;

import com.example.resource_service.application.dto.ConfigureResourceRequest;
import com.example.resource_service.application.dto.CreateResourceRequest;
import com.example.resource_service.application.port.in.ResourceUseCase;
import com.example.resource_service.domain.model.Resource;
import com.example.resource_service.presentation.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceUseCase resourceUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Resource> createResource(@RequestBody CreateResourceRequest request) {
        Resource resource = resourceUseCase.createResource(request);
        return ApiResponse.<Resource>builder().success(true).code("SUCCESS").message("Resource created").data(resource).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'EMPLOYEE')")
    public ApiResponse<List<Resource>> listResources(@RequestParam(required = false) String type) {
        List<Resource> resources = resourceUseCase.listResources(type);
        return ApiResponse.<List<Resource>>builder().success(true).code("SUCCESS").message("List resources").data(resources).build();
    }

    @GetMapping("/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'EMPLOYEE')")
    public ApiResponse<Resource> getResource(@PathVariable String resourceId) {
        Resource resource = resourceUseCase.getResource(resourceId);
        return ApiResponse.<Resource>builder().success(true).code("SUCCESS").message("Get resource").data(resource).build();
    }

    @PatchMapping("/{resourceId}/configuration")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Resource> configureResource(@PathVariable String resourceId, @RequestBody ConfigureResourceRequest request) {
        Resource resource = resourceUseCase.configureResource(resourceId, request);
        return ApiResponse.<Resource>builder().success(true).code("SUCCESS").message("Configured").data(resource).build();
    }
}
