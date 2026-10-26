package com.dev.foryourwishes.wishlist;

import com.dev.foryourwishes.user.User;
import com.dev.foryourwishes.user.UserRepository;
import com.dev.foryourwishes.wishlist.entity.Reservation;
import com.dev.foryourwishes.wishlist.entity.Wish;
import com.dev.foryourwishes.wishlist.entity.Wishlist;
import com.dev.foryourwishes.wishlist.repository.ReservationRepository;
import com.dev.foryourwishes.wishlist.repository.WishRepository;
import com.dev.foryourwishes.wishlist.repository.WishlistRepository;
import com.dev.foryourwishes.wishlist.service.WishManagerService;
import com.dev.foryourwishes.wishlist.service.WishlistManagerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers
class WishlistManagerServiceIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    private WishlistManagerService wishlistManagerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishManagerService wishManagerService;

    @Test
    void shouldDeleteWishlistWithWishes() {
        User owner = userRepository.save(new User("owner@test.ru", "owner", "hash"));
        Wishlist wishlist = wishlistRepository.save(new Wishlist("wishlist", "-", owner));
        Wish wish = wishRepository.save(new Wish("wish", "-", "https://example", wishlist));

        Long wishlistId = wishlist.getId();
        Long wishId = wish.getId();
        wishlistManagerService.deleteWishlist(wishlist.getId(), owner.getId());

        assertFalse(wishlistRepository.existsById(wishlistId));
        assertFalse(wishRepository.existsById(wishId));
    }

    @AfterEach
    void cleanDB() {
        reservationRepository.deleteAll();
        wishRepository.deleteAll();
        wishlistRepository.deleteAll();
        userRepository.deleteAll();
    }
}
