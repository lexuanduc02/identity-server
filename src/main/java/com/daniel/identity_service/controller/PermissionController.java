package com.daniel.identity_service.controller;

import com.daniel.identity_service.dto.request.PermissionCreateRequest;
import com.daniel.identity_service.dto.response.ApiResponse;
import com.daniel.identity_service.dto.response.PermissionDto;
import com.daniel.identity_service.service.PermissionService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionDto> addPermission(@RequestBody PermissionCreateRequest request) throws JOSEException {
        var createdPermission = permissionService.create(request);
        return ApiResponse.<PermissionDto>builder()
                .code(201)
                .message("Permission created successfully")
                .data(createdPermission)
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionDto>> getAllPermissions() {
        var permissions = permissionService.getAll();
        return ApiResponse.<List<PermissionDto>>builder()
                .code(200)
                .message("Permissions retrieved successfully")
                .data(permissions)
                .build();
    }

    @DeleteMapping("/{name}")
    public ApiResponse<Void> deletePermission(@PathVariable String name) {
        permissionService.deleteByName(name);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Permission deleted successfully")
                .build();
    }
}
