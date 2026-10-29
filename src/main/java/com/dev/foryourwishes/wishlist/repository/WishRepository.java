package com.dev.foryourwishes.wishlist.repository;

import com.dev.foryourwishes.wishlist.entity.Wish;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findAllByWishlistId(Long wishlistId);
    void deleteAllByWishlistId(Long wishlistId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wish> findWithLockById(Long id);
}
