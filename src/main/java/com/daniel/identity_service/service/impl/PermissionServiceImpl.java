package com.daniel.identity_service.service.impl;

import com.daniel.identity_service.dto.request.PermissionCreateRequest;
import com.daniel.identity_service.dto.response.PermissionDto;
import com.daniel.identity_service.mapper.PermissionMapper;
import com.daniel.identity_service.repository.PermissionRepository;
import com.daniel.identity_service.service.PermissionService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionDto create(PermissionCreateRequest request) throws JOSEException {
        var permission = permissionMapper.toPermission(request);
        var savedPermission = permissionRepository.save(permission);
        return permissionMapper.toPermissionDto(savedPermission);
    }

    @Override
    public List<PermissionDto> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(permissionMapper::toPermissionDto)
                .toList();
    }

    @Override
    public void deleteByName(String name) {
        permissionRepository.deleteById(name);
    }
}
