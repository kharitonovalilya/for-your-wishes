package com.dev.foryourwishes.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Login must not be blank")
        @Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
        @Pattern(
                regexp = "^[A-Za-z0-9._]+$",
                message = "Login may contain only Latin letters, digits, dot and underscore"
        )
        String login,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        String password) {}