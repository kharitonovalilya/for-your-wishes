package com.dev.foryourwishes.wishlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWishlistRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String title,

        @Size(max = 250)
        String description) { }
