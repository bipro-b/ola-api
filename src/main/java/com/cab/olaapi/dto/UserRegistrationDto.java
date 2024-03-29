package com.cab.olaapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserRegistrationDto(

        @NotEmpty(message = "User name can not be empty")
        String userName,
        String userMobileNo,

        @Email(message = "Invalid format")
        String userEmail,

        @NotEmpty(message = "User password cannot bet empty")
        String userPassword,

        @NotEmpty(message = "User role must not be empty")
        String userRole

) {
}
