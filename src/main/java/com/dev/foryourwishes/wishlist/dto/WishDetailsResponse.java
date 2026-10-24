package com.dev.foryourwishes.wishlist.dto;

import com.dev.foryourwishes.wishlist.WishStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WishDetailsResponse(Long id, String title, String description, String url, WishStatus status, Boolean reserved) {}