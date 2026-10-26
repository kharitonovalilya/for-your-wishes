package com.dev.foryourwishes.wishlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

@Validated
public record EditWishlistRequest(
        @NotNull
        @NotBlank
        @Size(min = 3, max = 50)
        String newTitle,

        @Size(max = 250)
        String newDescription) {}
