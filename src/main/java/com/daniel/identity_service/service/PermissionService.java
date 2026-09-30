package com.daniel.identity_service.service;

import com.daniel.identity_service.dto.request.PermissionCreateRequest;
import com.daniel.identity_service.dto.response.PermissionDto;
import com.nimbusds.jose.JOSEException;

import java.util.List;

public interface PermissionService {
    PermissionDto create(PermissionCreateRequest request) throws JOSEException;

    List<PermissionDto> getAll();

    void deleteByName(String name);
}
