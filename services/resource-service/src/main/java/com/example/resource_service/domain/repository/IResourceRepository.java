package com.example.resource_service.domain.repository;

import com.example.resource_service.domain.model.Resource;

import java.util.List;
import java.util.Optional;

public interface IResourceRepository {
    Resource save(Resource resource);
    Optional<Resource> findById(String id);
    List<Resource> findByType(String type);
    List<Resource> findAll();
}
