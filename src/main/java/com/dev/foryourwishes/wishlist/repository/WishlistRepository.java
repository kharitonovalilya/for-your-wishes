package com.dev.foryourwishes.wishlist.repository;

import com.dev.foryourwishes.wishlist.WishlistStatus;
import com.dev.foryourwishes.wishlist.entity.Wish;
import com.dev.foryourwishes.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findAllByOwnerIdAndStatus(
            Long ownerId,
            WishlistStatus status
    );
    List<Wishlist> findAllByOwnerId(Long ownerId);
}
