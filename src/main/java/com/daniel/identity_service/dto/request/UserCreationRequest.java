package com.daniel.identity_service.dto.request;

import com.daniel.identity_service.validator.DobConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    private String username;

    @Size(min = 8, max = 255, message = "INVALID_PASSWORD")
    String password;
    String firstName;
    String lastName;

    @NotNull(message = "Dob cannot be null")
    @DobConstraint(min = 15, message = "INVALID_DOB")
    LocalDate dob; // Date of Birth in ISO format (YYYY-MM-DD)

    List<String> roles; // List of role names
}
