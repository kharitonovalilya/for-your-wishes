package com.dev.foryourwishes.wishlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

@Validated
public record EditWishRequest(
        @NotNull
        @NotBlank
        @Size(min = 3, max = 50)
        String newTitle,

        @Size(max = 250)
        String newDescription,

        @URL
        String newUrl) {}
