package com.dev.foryourwishes.wishlist.dto;

import com.dev.foryourwishes.wishlist.WishlistStatus;

import java.util.List;

public record WishlistDetailsResponse(Long id, String title,
                                      String description, WishlistStatus status,
                                      List<WishDetailsResponse> wishes) { }