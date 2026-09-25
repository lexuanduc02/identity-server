package com.daniel.identity_service.exception;

public enum ErrorCode {
    UNCATEGORIZED_ERROR(9999, "Uncategorized error"),
    USER_EXISTS(1001, "User already exists"),
    USER_NOT_FOUND(1002, "User not found"),
    INVALID_CREDENTIALS(1003, "Invalid credentials"),
    INVALID_TOKEN(1004, "Invalid token"),
    UNAUTHORIZED(1005, "Unauthorized access"),
    INTERNAL_SERVER_ERROR(1006, "Internal server error"),
    INVALID_INPUT(1007, "Invalid input"),
    RESOURCE_NOT_FOUND(1008, "Resource not found"),
    FORBIDDEN(1009, "Forbidden access"),
    BAD_REQUEST(1010, "Bad request"),
    CONFLICT(1011, "Conflict error"),
    SERVICE_UNAVAILABLE(1012, "Service unavailable"),
    INVALID_INPUT_FORMAT(1013, "Invalid input format"),;

    ErrorCode(int code, String message) {;
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
