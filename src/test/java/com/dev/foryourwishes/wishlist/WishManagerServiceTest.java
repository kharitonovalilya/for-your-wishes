package com.dev.foryourwishes.wishlist;

import com.dev.foryourwishes.user.User;
import com.dev.foryourwishes.user.UserManagerService;
import com.dev.foryourwishes.wishlist.entity.Reservation;
import com.dev.foryourwishes.wishlist.entity.Wish;
import com.dev.foryourwishes.wishlist.entity.Wishlist;
import com.dev.foryourwishes.wishlist.exception.OwnWishReservationException;
import com.dev.foryourwishes.wishlist.exception.ReservationAccessDeniedException;
import com.dev.foryourwishes.wishlist.exception.WishFulfilledException;
import com.dev.foryourwishes.wishlist.exception.WishReservedException;
import com.dev.foryourwishes.wishlist.exception.WishlistArchivedException;
import com.dev.foryourwishes.wishlist.repository.ReservationRepository;
import com.dev.foryourwishes.wishlist.repository.WishRepository;
import com.dev.foryourwishes.wishlist.service.WishManagerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishManagerServiceTest {

    private static final Long WISH_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final String WISH_URL = "https://example.com";

    @Mock
    private WishRepository wishRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserManagerService userManagerService;
    @InjectMocks
    private WishManagerService wishManagerService;

    @Test
    void shouldNotReserveFulfilledWish() {
        Wish wish = createWish();
        wish.markAsFulfilled();

        when(wishRepository.findById(WISH_ID))
                .thenReturn(Optional.of(wish));

        assertThrows(
                WishFulfilledException.class,
                () -> wishManagerService.reserveWish(WISH_ID, USER_ID)
        );

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void shouldNotReserveOwnWish() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(USER_ID);

        Wish wish = createWish(owner);

        when(wishRepository.findById(WISH_ID))
                .thenReturn(Optional.of(wish));

        assertThrows(
                OwnWishReservationException.class,
                () -> wishManagerService.reserveWish(WISH_ID, USER_ID)
        );

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void shouldNotReserveAlreadyReservedWish() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(OTHER_USER_ID);

        Wish wish = createWish(owner);

        when(wishRepository.findById(WISH_ID))
                .thenReturn(Optional.of(wish));

        when(reservationRepository.existsByWishId(WISH_ID))
                .thenReturn(true);

        assertThrows(
                WishReservedException.class,
                () -> wishManagerService.reserveWish(WISH_ID, USER_ID)
        );

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void shouldNotReserveWishFromArchivedWishlist() {
        Wishlist wishlist = createWishlist();
        wishlist.archive();

        Wish wish = createWish(wishlist);

        when(wishRepository.findById(WISH_ID))
                .thenReturn(Optional.of(wish));

        assertThrows(
                WishlistArchivedException.class,
                () -> wishManagerService.reserveWish(WISH_ID, USER_ID)
        );

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void shouldNotCancelAnotherUsersReservation() {
        Wish wish = createWish();

        User reserver = mock(User.class);
        when(reserver.getId()).thenReturn(OTHER_USER_ID);

        Reservation reservation = new Reservation(wish, reserver);

        when(wishRepository.findById(WISH_ID))
                .thenReturn(Optional.of(wish));

        when(reservationRepository.findByWishId(WISH_ID))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationAccessDeniedException.class,
                () -> wishManagerService.cancelReservation(WISH_ID, USER_ID)
        );

        verify(reservationRepository, never())
                .delete(any());
    }

    private User createOwner() {
        return new User("owner@test.ru", "owner", "hash");
    }

    private Wishlist createWishlist() {
        return createWishlist(createOwner());
    }

    private Wishlist createWishlist(User owner) {
        return new Wishlist("wishlist","-", owner);
    }

    private Wish createWish() {
        return createWish(createWishlist());
    }

    private Wish createWish(User owner) {
        return createWish(createWishlist(owner));
    }

    private Wish createWish(Wishlist wishlist) {
        return new Wish("wish", "-", WISH_URL, wishlist);
    }
}