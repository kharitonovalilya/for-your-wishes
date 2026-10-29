package com.dev.foryourwishes.wishlist.repository;

import com.dev.foryourwishes.wishlist.WishlistStatus;
import com.dev.foryourwishes.wishlist.entity.Wish;
import com.dev.foryourwishes.wishlist.entity.Wishlist;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findAllByOwnerIdAndStatus(
            Long ownerId,
            WishlistStatus status
    );
    List<Wishlist> findAllByOwnerId(Long ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wishlist> findWithLockById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select w.wishlist
    from Wish w
    where w.id = :wishId
""")
    Optional<Wishlist> findWithLockByWishId(@Param("wishId") Long wishId);
}
