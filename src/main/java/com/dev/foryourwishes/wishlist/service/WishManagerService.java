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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishManagerService {

    private final WishRepository wishRepository;
    private final ReservationRepository reservationRepository;
    private final UserManagerService userManagerService;

    public Wish findById(Long wishId) {
        return wishRepository.findById(wishId)
                .orElseThrow(() -> new WishNotFoundException(wishId));
    }

    @Transactional
    public void deleteWish(Long wishId, Long userId) {
        Wish wish = findById(wishId);
        checkOwner(wish, userId);
        checkWishlistNotArchived(wish);
        reservationRepository.deleteByWishId(wishId);
        wishRepository.delete(wish);
    }

    @Transactional
    public void editWish(Long wishId, String newTitle, String newDescription, String newUrl, Long userId) {
        Wish wish = findById(wishId);
        checkOwner(wish, userId);
        checkWishlistNotArchived(wish);
        wish.editWish(newTitle, newDescription, newUrl);
    }

    @Transactional
    public void markAsFulfilled(Long wishId, Long userId) {
        Wish wish = findById(wishId);
        checkOwner(wish, userId);
        checkWishlistNotArchived(wish);
        wish.markAsFulfilled();
        reservationRepository.deleteByWishId(wishId);
    }

    @Transactional
    public void reserveWish(Long wishId, Long userId) {
        Wish wish = findById(wishId);
        checkWishlistNotArchived(wish);
        if (wish.getStatus() == WishStatus.FULFILLED) {
            throw new WishFulfilledException(wishId);
        }
        Wishlist wishlist = wish.getWishlist();
        if (wishlist.getOwner().getId().equals(userId)) {
            throw new OwnWishReservationException(userId);
        }
        if (reservationRepository.existsByWishId(wishId)) {
            throw new WishReservedException(wishId);
        }
        User user = userManagerService.findById(userId);
        Reservation reservation = new Reservation(wish, user);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(Long wishId, Long userId) {
        Wish wish = findById(wishId);
        checkWishlistNotArchived(wish);
        Reservation reservation = reservationRepository.findByWishId(wishId)
                .orElseThrow(() -> new ReservationNotFoundException(wishId));
        if (!reservation.getReservedBy().getId().equals(userId)) {
            throw new ReservationAccessDeniedException(reservation.getId());
        }
        reservationRepository.delete(reservation);
    }

    private void checkOwner(Wish wish, Long userId) {
        Long ownerId = wish.getWishlist().getOwner().getId();
        if (!ownerId.equals(userId)) {
            throw new WishlistAccessDeniedException(wish.getWishlist().getId(), userId);
        }
    }

    private void checkWishlistNotArchived(Wish wish) {
        if (wish.getWishlist().getStatus() == WishlistStatus.ARCHIVED) {
            throw new WishlistArchivedException(wish.getWishlist().getId());
        }
    }

}
