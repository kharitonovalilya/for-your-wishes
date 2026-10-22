package com.dev.foryourwishes.wishlist.service;

import com.dev.foryourwishes.user.User;
import com.dev.foryourwishes.user.UserManagerService;
import com.dev.foryourwishes.wishlist.WishStatus;
import com.dev.foryourwishes.wishlist.WishlistStatus;
import com.dev.foryourwishes.wishlist.entity.Reservation;
import com.dev.foryourwishes.wishlist.entity.Wish;
import com.dev.foryourwishes.wishlist.entity.Wishlist;
import com.dev.foryourwishes.wishlist.exception.*;
import com.dev.foryourwishes.wishlist.repository.ReservationRepository;
import com.dev.foryourwishes.wishlist.repository.WishRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class WishManagerService {

    private final WishRepository wishRepository;
    private final ReservationRepository reservationRepository;
    private final UserManagerService userManagerService;

    public Wish findById(Long wishId) {
        return wishRepository.findById(wishId)
                .orElseThrow(() -> new WishNotFoundException(wishId));
    }

    @Transactional
    public void deleteWish(Long wishId) {
        reservationRepository.deleteByWishId(wishId);
        wishRepository.deleteById(wishId);
    }

    @Transactional
    public void editWish(Long wishId, String newTitle, String newDescription, String newUrl) {
        Wish wish = findById(wishId);
        Wishlist wishlist = wish.getWishlist();
        if (wishlist.getStatus() == WishlistStatus.ARCHIVED) {
            throw new WishlistArchivedException(wishlist.getId());
        }
        wish.editWish(newTitle, newDescription, newUrl);
    }

    @Transactional
    public void markAsFulfilled(Long wishId) {
        Wish wish = findById(wishId);
        Wishlist wishlist = wish.getWishlist();
        if (wishlist.getStatus() == WishlistStatus.ARCHIVED) {
            throw new WishlistArchivedException(wishlist.getId());
        }
        wish.markAsFulfilled();
        reservationRepository.deleteByWishId(wishId);
    }

    @Transactional
    public void reserveWish(Long wishId, Long userId) {
        Wish wish = findById(wishId);
        if (wish.getStatus() == WishStatus.FULFILLED) {
            throw new WishFulfilledException(wishId);
        }
        if (reservationRepository.existsByWishId(wishId)) {
            throw new WishReservedException(userId);
        }
        Wishlist wishlist = wish.getWishlist();
        if (wishlist.getStatus() == WishlistStatus.ARCHIVED) {
            throw new WishlistArchivedException(wishlist.getId());
        }
        User user = userManagerService.findById(userId);
        if (wishlist.getOwner().getId().equals(userId)) {
            throw new OwnWishReservationException(userId);
        }
        Reservation reservation = new Reservation(wish, user);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(Long wishId, Long userId) {
        Reservation reservation = reservationRepository.findByWishId(wishId)
                .orElseThrow(() -> new ReservationNotFoundException(wishId));
        if (!reservation.getReservedBy().getId().equals(userId)) {
            throw new ReservationAccessDeniedException(reservation.getId());
        }
        reservationRepository.delete(reservation);
    }

}
