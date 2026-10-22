package com.dev.foryourwishes.wishlist.controller;

import com.dev.foryourwishes.security.AuthUser;
import com.dev.foryourwishes.wishlist.WishlistStatus;
import com.dev.foryourwishes.wishlist.dto.*;
import com.dev.foryourwishes.wishlist.entity.Wishlist;
import com.dev.foryourwishes.wishlist.service.WishlistManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistManagerService wishlistManagerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistResponse createWishlist
            (@RequestBody CreateWishlistRequest request,
             @AuthenticationPrincipal AuthUser currentUser) {
        Wishlist wishlist = wishlistManagerService.createWishlist(
                currentUser.getId(),
                request.title(),
                request.description()
        );
        return new WishlistResponse(wishlist.getId(), wishlist.getTitle(), wishlist.getDescription());
    }

    @PatchMapping("/{wishlistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editWishlist(
            @RequestBody EditWishlistRequest request,
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishlistId
            ){
        wishlistManagerService.editWishlist(
                wishlistId,
                request.newTitle(),
                request.newDescription(),
                currentUser.getId()
        );
    }

    @GetMapping
    public List<WishlistSummaryResponse> getCurrentUserWishlists(
            @AuthenticationPrincipal AuthUser currentUser,
            @RequestParam(required = true) WishlistStatus status
    ) {
        return wishlistManagerService.getWishlistsByUserId(
                currentUser.getId(),
                status
        );
    }

    @DeleteMapping("/{wishlistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWishlist(
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishlistId
    ) {
        wishlistManagerService.deleteWishlist(wishlistId, currentUser.getId());
    }

    @PatchMapping("/{wishlistId}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveWishlist(
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishlistId
    ) {
        wishlistManagerService.archiveWishlist(wishlistId, currentUser.getId());
    }

    @PatchMapping("/{wishlistId}/unarchive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unarchiveWishlist(
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishlistId
    ) {
        wishlistManagerService.unarchiveWishlist(wishlistId, currentUser.getId());
    }

    @GetMapping("/{wishlistId}")
    public WishlistDetailsResponse getWishlist(
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishlistId
    ) {
        return wishlistManagerService.getWishlist(
                wishlistId,
                currentUser.getId()
        );
    }
}
