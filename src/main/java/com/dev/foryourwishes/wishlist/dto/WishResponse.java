package com.dev.foryourwishes.wishlist.dto;

import com.dev.foryourwishes.wishlist.WishStatus;

public record WishResponse(Long id, String title, WishStatus status) {}