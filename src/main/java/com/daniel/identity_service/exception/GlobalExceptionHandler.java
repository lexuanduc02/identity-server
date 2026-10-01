package com.daniel.identity_service.exception;

import com.daniel.identity_service.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleException() {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_ERROR;

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessException() {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new HashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            String enumKey = fieldError.getDefaultMessage();
            ErrorCode errorCode = ErrorCode.INVALID_KEY;
            Map<String, Object> attributes = null;

            // 1. Trích xuất attributes từ ConstraintViolation của Hibernate/Jakarta
            try {
                ConstraintViolation<?> violation = fieldError.unwrap(ConstraintViolation.class);
                attributes = violation.getConstraintDescriptor().getAttributes();
            } catch (Exception ignored) {
                // Bỏ qua nếu không unwrap được
            }

            // 2. Map sang ErrorCode
            try {
                if (enumKey != null) {
                    errorCode = ErrorCode.valueOf(enumKey);
                }
            } catch (IllegalArgumentException ignored) {
                // Fallback INVALID_KEY
            }

            // 3. Map các attribute vào message (ví dụ thay thế {min} bằng giá trị thực tế)
            String detailMessage = mapAttribute(errorCode.getMessage(), attributes);
            fieldErrors.put(fieldError.getField(), detailMessage);
        }

        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(ErrorCode.INVALID_KEY.getCode())
                .message(ErrorCode.INVALID_KEY.getMessage())
                .errors(fieldErrors)
                .build());
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Hàm helper thay thế các placeholder {attributeName} trong message
    private String mapAttribute(String message, Map<String, Object> attributes) {
        if (attributes == null || message == null) {
            return message;
        }

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (message.contains(placeholder)) {
                message = message.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return message;
    }
}
