package com.daniel.identity_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String password;
    private String firstName;
    private String lastName;
    private String dob; // Date of Birth in ISO format (YYYY-MM-DD)
    List<String> roles; // List of role names
}
