package com.daniel.identity_service.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_ERROR(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTS(1001, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(1003, "Invalid credentials", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1004, "Invalid token", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR(1006, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT(1007, "Invalid input", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(1008, "Resource not found", HttpStatus.NOT_FOUND),
    FORBIDDEN(1009, "Forbidden access", HttpStatus.FORBIDDEN),
    BAD_REQUEST(1010, "Bad request", HttpStatus.BAD_REQUEST),
    CONFLICT(1011, "Conflict error", HttpStatus.CONFLICT),
    SERVICE_UNAVAILABLE(1012, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    INVALID_INPUT_FORMAT(1013, "Invalid input format", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(1014, "User does not exist", HttpStatus.NOT_FOUND),
    USER_AUTHENTICATION_FAILED(1015, "User authentication failed", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1016, "Forbidden access", HttpStatus.FORBIDDEN),

    // Validation Errors
    INVALID_KEY(1009, "Invalid key", HttpStatus.BAD_REQUEST),
    DOB_REQUIRED(1010, "Date of birth is required", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1011, "Your age must be at least {min}", HttpStatus.BAD_REQUEST);;

    int code;
    String message;
    HttpStatusCode statusCode;
}