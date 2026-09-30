package com.daniel.identity_service.service.impl;

import com.daniel.identity_service.dto.request.RoleRequest;
import com.daniel.identity_service.dto.response.RoleDto;
import com.daniel.identity_service.entity.Permission;
import com.daniel.identity_service.entity.Role;
import com.daniel.identity_service.mapper.RoleMapper;
import com.daniel.identity_service.repository.PermissionRepository;
import com.daniel.identity_service.repository.RoleRepository;
import com.daniel.identity_service.service.RoleService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @Override
    public RoleDto create(RoleRequest request) throws JOSEException {
        Role role = roleMapper.toRole(request);

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<Permission>(permissions));

        roleRepository.save(role);
        return roleMapper.toRoleDto(role);
    }

    @Override
    public List<RoleDto> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toRoleDto)
                .toList();
    }

    @Override
    public void delete(String id) {
        roleRepository.deleteById(id);
    }
}
