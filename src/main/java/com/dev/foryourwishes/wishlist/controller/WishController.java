package com.dev.foryourwishes.wishlist.controller;

import com.dev.foryourwishes.security.AuthUser;
import com.dev.foryourwishes.wishlist.dto.EditWishRequest;
import com.dev.foryourwishes.wishlist.service.WishManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishes")
@RequiredArgsConstructor
public class WishController {

    private final WishManagerService wishManagerService;

    @PatchMapping("/{wishId}")
    public void editWish(
            @PathVariable Long wishId,
            @RequestBody EditWishRequest request,
            @AuthenticationPrincipal AuthUser currentUser
            ) {
        wishManagerService.editWish(wishId,
                request.newTitle(), request.newDescription(), request.newUrl(),
                currentUser.getId());
    }

    @DeleteMapping("/{wishId}")
    public void deleteWish(
            @PathVariable Long wishId,
            @AuthenticationPrincipal AuthUser currentUser
    ) {
        wishManagerService.deleteWish(wishId, currentUser.getId());
    }

    @PutMapping("/{wishId}/fulfill")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsFulfilled(
            @PathVariable Long wishId,
            @AuthenticationPrincipal AuthUser currentUser
    ) {
        wishManagerService.markAsFulfilled(wishId, currentUser.getId());
    }

    @PutMapping("/{wishId}/reservation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserveWish(
            @PathVariable Long wishId,
            @AuthenticationPrincipal AuthUser currentUser
    ) {
        wishManagerService.reserveWish(wishId, currentUser.getId());
    }

    @DeleteMapping("/{wishId}/reserve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(
            @PathVariable Long wishId,
            @AuthenticationPrincipal AuthUser currentUser
    ) {
        wishManagerService.cancelReservation(wishId, currentUser.getId());
    }

}
