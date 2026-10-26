package com.dev.foryourwishes.wishlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CreateWishRequest(
        @NotNull
        @NotBlank
        @Size(min = 3, max = 50)
        String title,

        @Size(max = 250)
        String description,

        @URL
        String url) {}
