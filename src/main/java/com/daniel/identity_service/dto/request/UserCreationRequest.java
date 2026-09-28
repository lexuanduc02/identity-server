package com.daniel.identity_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    private String username;

    @Size(min = 8, message = "INVALID_INPUT_FORMAT_DD")
    private String password;

    private String firstName;
    private String lastName;
    private String dob; // Date of Birth in ISO format (YYYY-MM-DD)
}
