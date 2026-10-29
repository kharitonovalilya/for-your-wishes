package com.dev.foryourwishes.wishlist;

import com.dev.foryourwishes.user.User;
import com.dev.foryourwishes.user.UserRepository;
import com.dev.foryourwishes.wishlist.entity.Reservation;
import com.dev.foryourwishes.wishlist.entity.Wish;
import com.dev.foryourwishes.wishlist.entity.Wishlist;
import com.dev.foryourwishes.wishlist.exception.*;
import com.dev.foryourwishes.wishlist.repository.ReservationRepository;
import com.dev.foryourwishes.wishlist.repository.WishRepository;
import com.dev.foryourwishes.wishlist.repository.WishlistRepository;
import com.dev.foryourwishes.wishlist.service.WishManagerService;
import com.dev.foryourwishes.wishlist.service.WishlistManagerService;
import org.apache.catalina.Executor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class WishManagerServiceIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest");

    @Autowired
    private WishManagerService wishManagerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistManagerService wishlistManagerService;

    @Test
    void shouldReserveAvailableWish() {
        User owner = userRepository.save(new User("owner@test.ru", "owner", "hash"));
        User reserver = userRepository.save(new User("reserver@test.ru", "reserver", "hash"));
        Wishlist wishlist = wishlistRepository.save(new Wishlist("wishlist", "-", owner));
        Wish wish = wishRepository.save(new Wish("wish", "-", "https://example", wishlist));

        wishManagerService.reserveWish(wish.getId(), reserver.getId());

        Optional<Reservation> reservation = reservationRepository.findByWishId(wish.getId());
        assertTrue(reservation.isPresent());
        Reservation savedReservation = reservation.get();
        assertEquals(reserver.getId(), savedReservation.getReservedBy().getId());
        assertEquals(wish.getId(), savedReservation.getWish().getId());
    }

    @Test
    void shouldMarkWishAsFulfilledAndDeleteReservation() {
        User owner = userRepository.save(new User("owner@test.ru", "owner", "hash"));
        User reserver = userRepository.save(new User("reserver@test.ru", "reserver", "hash"));
        Wishlist wishlist = wishlistRepository.save(new Wishlist("wishlist", "-", owner));
        Wish wish = wishRepository.save(new Wish("wish", "-", "https://example", wishlist));
        wishManagerService.reserveWish(wish.getId(), reserver.getId());

        wishManagerService.markAsFulfilled(wish.getId(), owner.getId());
        Wish updatedWish = wishRepository.findById(wish.getId())
                .orElseThrow();

        assertEquals(WishStatus.FULFILLED, updatedWish.getStatus());
        Optional<Reservation> reservation = reservationRepository.findByWishId(updatedWish.getId());
        assertFalse(reservation.isPresent());
    }

    @Test
    void shouldAllowOnlyOneReservationWhenRequestsAreConcurrent() throws InterruptedException, ExecutionException {
        User owner = userRepository.save(new User("owner@test.ru", "owner", "hash"));
        User reserver1 = userRepository.save(new User("reserver1@test.ru", "reserver1", "hash"));
        User reserver2 = userRepository.save(new User("reserver2@test.ru", "reserver2", "hash"));
        Wishlist wishlist = wishlistRepository.save(new Wishlist("wishlist", "-", owner));
        Wish wish = wishRepository.save(new Wish("wish", "-", "https://example", wishlist));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successfulReservations = new AtomicInteger();
        AtomicInteger failedReservations = new AtomicInteger();
        List<User> reservers = List.of(reserver1, reserver2);
        List<Future<?>> futures = new ArrayList<>();

        for(User reserver : reservers){
            Future<?> future = executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    wishManagerService.reserveWish(wish.getId(), reserver.getId());
                    successfulReservations.incrementAndGet();
                } catch (WishReservedException e) {
                    failedReservations.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }
        ready.await();
        start.countDown();

        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();

        Optional<Reservation> reservation = reservationRepository.findByWishId(wish.getId());

        assertTrue(reservation.isPresent());
        assertEquals(1, successfulReservations.get());
        assertEquals(1, failedReservations.get());
    }

    @Test
    void shouldKeepFulfilledWishWithoutReservationWhenReserveAndFulfillAreConcurrent() throws InterruptedException, ExecutionException {
        User owner = userRepository.save(new User("owner@test.ru", "owner", "hash"));
        User reserver = userRepository.save(new User("reserver@test.ru", "reserver", "hash"));
        Wishlist wishlist = wishlistRepository.save(new Wishlist("wishlist", "-", owner));
        Wish wish = wishRepository.save(new Wish("wish", "-", "https://example", wishlist));

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        futures.add(executorService.submit(() -> {
            ready.countDown();
            try {
                start.await();
                wishManagerService.reserveWish(wish.getId(), reserver.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            catch (WishFulfilledException e) {
                return;
            }
        }));

        futures.add(executorService.submit(() -> {
            ready.countDown();
            try {
                start.await();
                wishManagerService.markAsFulfilled(wish.getId(), owner.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }));

        ready.await();
        start.countDown();
        for(Future<?> future : futures) {
            future.get();
        }
        executorService.shutdown();

        Wish updatedWish = wishRepository.findById(wish.getId())
                .orElseThrow();
        assertEquals(WishStatus.FULFILLED, updatedWish.getStatus());
        Optional<Reservation> reservation = reservationRepository.findByWishId(updatedWish.getId());
        assertFalse(reservation.isPresent());
    }

    @Test
    void shouldBeNoReservationWhenWishlistIsArchived() throws InterruptedException, ExecutionException {
        User owner = userRepository.save(new User("owner@test.ru", "owner", "hash"));
        User reserver = userRepository.save(new User("reserver@test.ru", "reserver", "hash"));
        Wishlist wishlist = wishlistRepository.save(new Wishlist("wishlist", "-", owner));
        Wish wish = wishRepository.save(new Wish("wish", "-", "https://example", wishlist));

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        futures.add(executorService.submit(() -> {
            ready.countDown();
            try {
                start.await();
                wishManagerService.reserveWish(wish.getId(), reserver.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            catch (WishlistArchivedException e){
                return;
            }
        }));

        futures.add(executorService.submit(() -> {
            ready.countDown();
            try {
                start.await();
                wishlistManagerService.archiveWishlist(wishlist.getId(), owner.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }));

        ready.await();
        start.countDown();
        for(Future<?> future : futures) {
            future.get();
        }
        executorService.shutdown();

        Wishlist updatedWishlist = wishlistRepository.findById(wishlist.getId())
                .orElseThrow();
        assertEquals(WishlistStatus.ARCHIVED, updatedWishlist.getStatus());
        Optional<Reservation> reservation = reservationRepository.findByWishId(wish.getId());
        assertFalse(reservation.isPresent());
    }

    @AfterEach
    void cleanDB() {
        reservationRepository.deleteAll();
        wishRepository.deleteAll();
        wishlistRepository.deleteAll();
        userRepository.deleteAll();
    }
}
