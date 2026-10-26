package com.dev.foryourwishes.wishlist.controller;

import com.dev.foryourwishes.security.AuthUser;
import com.dev.foryourwishes.wishlist.dto.EditWishRequest;
import com.dev.foryourwishes.wishlist.service.WishManagerService;
import jakarta.validation.Valid;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editWish(
            @RequestBody @Valid EditWishRequest request,
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishId
    ) {
        wishManagerService.editWish(wishId,
                request.newTitle(), request.newDescription(), request.newUrl(),
                currentUser.getId());
    }

    @DeleteMapping("/{wishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWish(
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishId
    ) {
        wishManagerService.deleteWish(wishId, currentUser.getId());
    }

    @PostMapping("/{wishId}/fulfill")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsFulfilled(
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishId
    ) {
        wishManagerService.markAsFulfilled(wishId, currentUser.getId());
    }

    @PutMapping("/{wishId}/reservation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserveWish(
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishId
    ) {
        wishManagerService.reserveWish(wishId, currentUser.getId());
    }

    @DeleteMapping("/{wishId}/reservation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(
            @AuthenticationPrincipal AuthUser currentUser,
            @PathVariable Long wishId
    ) {
        wishManagerService.cancelReservation(wishId, currentUser.getId());
    }

}
