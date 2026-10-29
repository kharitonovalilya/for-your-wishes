package com.dev.foryourwishes.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(max = 20)
        String login,

        @Size(max = 25)
        String password) {}