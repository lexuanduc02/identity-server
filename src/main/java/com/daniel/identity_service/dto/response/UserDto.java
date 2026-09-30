package com.daniel.identity_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    String id;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    HashSet<RoleDto> roles;
}
