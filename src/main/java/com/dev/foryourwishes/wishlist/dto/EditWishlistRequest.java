package com.dev.foryourwishes.wishlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditWishlistRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String newTitle,

        @Size(max = 250)
        String newDescription) {}
