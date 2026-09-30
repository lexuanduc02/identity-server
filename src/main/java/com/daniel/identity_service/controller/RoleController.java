package com.daniel.identity_service.controller;

import com.daniel.identity_service.dto.request.RoleRequest;
import com.daniel.identity_service.dto.response.ApiResponse;
import com.daniel.identity_service.dto.response.RoleDto;
import com.daniel.identity_service.service.RoleService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleDto>> getAll() {
        return ApiResponse.<List<RoleDto>>builder()
                .code(200)
                .message("Roles retrieved successfully")
                .data(roleService.getAll())
                .build();
    }

    @PostMapping
    public ApiResponse<RoleDto> createRole(@RequestBody RoleRequest request) throws JOSEException {
        return ApiResponse.<RoleDto>builder()
                .code(201)
                .message("Role created successfully")
                .data(roleService.create(request)) // Replace with actual created role data
                .build();
    }

    @DeleteMapping("/{name}")
    public ApiResponse<Void> deleteRole(@PathVariable String name) {
        roleService.delete(name);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Role deleted successfully")
                .build();
    }
}
