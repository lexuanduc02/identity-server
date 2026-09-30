package com.daniel.identity_service.mapper;

import com.daniel.identity_service.dto.request.UserCreationRequest;
import com.daniel.identity_service.dto.request.UserUpdateRequest;
import com.daniel.identity_service.dto.response.UserDto;
import com.daniel.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "roles", ignore = true),
    })
    User toUser(UserCreationRequest request);

    UserDto toUserDto(User user);

    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "roles", ignore = true),
    })
    void updateUserFromRequest(@MappingTarget User user, UserUpdateRequest request);
}
