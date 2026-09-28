package com.daniel.identity_service.service.impl;

import com.daniel.identity_service.dto.request.UserCreationRequest;
import com.daniel.identity_service.dto.request.UserUpdateRequest;
import com.daniel.identity_service.dto.response.UserDto;
import com.daniel.identity_service.entity.User;
import com.daniel.identity_service.exception.AppException;
import com.daniel.identity_service.exception.ErrorCode;
import com.daniel.identity_service.mapper.UserMapper;
import com.daniel.identity_service.repository.UserRepository;
import com.daniel.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    @Override
    public UserDto createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
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

        return userMapper.toUserDto(userRepository.save(existingUser));
    }

    @Override
    public  void deleteUser(String userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(existingUser);
    }
}
