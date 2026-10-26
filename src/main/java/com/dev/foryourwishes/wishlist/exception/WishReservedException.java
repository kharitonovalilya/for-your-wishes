package com.dev.foryourwishes.wishlist.exception;

public class WishReservedException extends RuntimeException {
    public WishReservedException(Long wishId) {
        super("Wish with id = %d is already reserved".formatted(wishId));
    }
}
