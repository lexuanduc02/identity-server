package com.daniel.identity_service.service;

import com.daniel.identity_service.dto.request.RoleRequest;
import com.daniel.identity_service.dto.response.RoleDto;
import com.nimbusds.jose.JOSEException;

import java.util.List;

public interface RoleService {
    RoleDto create(RoleRequest request) throws JOSEException;

    List<RoleDto> getAll();

    void delete(String id);
}
