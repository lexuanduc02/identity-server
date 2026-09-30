package com.daniel.identity_service.service.impl;

import com.daniel.identity_service.dto.request.UserCreationRequest;
import com.daniel.identity_service.dto.request.UserUpdateRequest;
import com.daniel.identity_service.dto.response.UserDto;
import com.daniel.identity_service.entity.Role;
import com.daniel.identity_service.entity.User;
import com.daniel.identity_service.exception.AppException;
import com.daniel.identity_service.exception.ErrorCode;
import com.daniel.identity_service.mapper.UserMapper;
import com.daniel.identity_service.repository.RoleRepository;
import com.daniel.identity_service.repository.UserRepository;
import com.daniel.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(request.getRoles());
            user.setRoles(new HashSet<>(roles));
        }

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @Override
    public UserDto getUserById(String userId) {
        return userMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public UserDto updateUser(String userId, UserUpdateRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        userMapper.updateUserFromRequest(existingUser, request);

        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(request.getRoles());
            existingUser.setRoles(new HashSet<>(roles));
        }

        return userMapper.toUserDto(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(String userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(existingUser);
    }
}
