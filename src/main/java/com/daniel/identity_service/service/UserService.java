package com.daniel.identity_service.service;

import com.daniel.identity_service.dto.request.UserCreationRequest;
import com.daniel.identity_service.dto.request.UserUpdateRequest;
import com.daniel.identity_service.dto.response.ApiResponse;
import com.daniel.identity_service.dto.response.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreationRequest request);
    List<UserDto> getUsers();
    UserDto getUserById(String userId);
    UserDto updateUser(String userId, UserUpdateRequest request);
    void deleteUser(String userId);
}
