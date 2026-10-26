package com.dev.foryourwishes.wishlist.repository;

import com.dev.foryourwishes.wishlist.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByWishId(Long wishId);
    boolean existsByWishId(Long wishId);
    void deleteAllByWishWishlistId(Long wishlistId);
    void deleteByWishId(Long wishId);

    @Query("""
    select r.wish.id
    from Reservation r
    where r.wish.wishlist.id = :wishlistId
""")
    List<Long> findReservedWishIdsByWishlistId(Long wishlistId);
}
