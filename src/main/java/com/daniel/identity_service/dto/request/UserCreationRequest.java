package com.daniel.identity_service.dto.request;

import jakarta.validation.constraints.Size;

public class UserCreationRequest {
    private String username;

    @Size(min = 8, message = "INVALID_INPUT_FORMAT_DD")
    private String password;

    private String firstName;
    private String lastName;
    private String dob; // Date of Birth in ISO format (YYYY-MM-DD)

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
