package com.dev.foryourwishes.wishlist.exception;

// TODO don't know what id should be provided
public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(Long wishId) {
        super("Reservation for wish with id " + wishId + " not found");
    }
}
