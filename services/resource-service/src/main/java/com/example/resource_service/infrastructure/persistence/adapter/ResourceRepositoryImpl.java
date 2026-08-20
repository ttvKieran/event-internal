package com.example.resource_service.infrastructure.persistence.adapter;

import com.example.resource_service.domain.model.Resource;
import com.example.resource_service.domain.repository.IResourceRepository;
import com.example.resource_service.infrastructure.persistence.mapper.ResourceMapper;
import com.example.resource_service.infrastructure.persistence.repository.ResourceSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ResourceRepositoryImpl implements IResourceRepository {
    private final ResourceSpringDataRepository jpaRepository;
    private final ResourceMapper mapper;

    @Override
    public Resource save(Resource resource) {
        var entity = mapper.toEntity(resource);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Resource> findById(String id) {
        return jpaRepository.findById(java.util.UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    public List<Resource> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Resource> findByType(String type) {
        return jpaRepository.findByType(type).stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
