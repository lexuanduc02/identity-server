package com.daniel.identity_service.mapper;

import com.daniel.identity_service.dto.request.PermissionCreateRequest;
import com.daniel.identity_service.dto.response.PermissionDto;
import com.daniel.identity_service.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionCreateRequest request);

    PermissionDto toPermissionDto(Permission permission);
}
