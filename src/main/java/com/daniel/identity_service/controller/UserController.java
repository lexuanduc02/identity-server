package com.daniel.identity_service.controller;

import com.daniel.identity_service.dto.request.UserCreationRequest;
import com.daniel.identity_service.dto.request.UserUpdateRequest;
import com.daniel.identity_service.dto.response.ApiResponse;
import com.daniel.identity_service.dto.response.UserDto;
import com.daniel.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserDto> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserDto>> getUsers() {
        return ApiResponse.<List<UserDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .data(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserDto> getUserById(@PathVariable String userId) {
        return ApiResponse.<UserDto>builder()
                .code(HttpStatus.OK.value())
                .message("User retrieved successfully")
                .data(userService.getUserById(userId))
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserDto> updateUser(
            @PathVariable String userId,
            @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserDto>builder()
                .code(HttpStatus.OK.value())
                .message("User updated successfully")
                .data(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("User deleted successfully")
                .build();
    }
}