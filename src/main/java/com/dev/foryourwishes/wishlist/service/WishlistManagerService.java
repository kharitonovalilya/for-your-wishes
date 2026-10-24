package com.dev.foryourwishes.wishlist.service;

import com.dev.foryourwishes.user.User;
import com.dev.foryourwishes.user.UserManagerService;
import com.dev.foryourwishes.wishlist.WishlistStatus;
import com.dev.foryourwishes.wishlist.dto.WishDetailsResponse;
import com.dev.foryourwishes.wishlist.dto.WishlistDetailsResponse;
import com.dev.foryourwishes.wishlist.dto.WishlistSummaryResponse;
import com.dev.foryourwishes.wishlist.entity.Wish;
import com.dev.foryourwishes.wishlist.entity.Wishlist;
import com.dev.foryourwishes.wishlist.exception.WishlistAccessDeniedException;
import com.dev.foryourwishes.wishlist.exception.WishlistArchivedException;
import com.dev.foryourwishes.wishlist.exception.WishlistNotFoundException;

import com.dev.foryourwishes.wishlist.repository.ReservationRepository;
import com.dev.foryourwishes.wishlist.repository.WishRepository;
import com.dev.foryourwishes.wishlist.repository.WishlistRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WishlistManagerService {

    private final WishlistRepository wishlistRepository;
    private final WishRepository wishRepository;
    private final UserManagerService userManagerService;
    private final ReservationRepository reservationRepository;

    public Wishlist findById(Long wishlistId) {
        return wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new WishlistNotFoundException(wishlistId));
    }

    @Transactional
    public Wishlist createWishlist(Long ownerId, String title, String description) {
        User owner = userManagerService.findById(ownerId);
        Wishlist newWishlist = new Wishlist(title, description, owner);
        return wishlistRepository.save(newWishlist);
    }

    @Transactional
    public Wish addWishToWishlist(Long wishlistId, String title, String description, String url, Long userId) {
        Wishlist wishlist = findById(wishlistId);
        checkOwner(wishlist, userId);
        if (wishlist.getStatus() == WishlistStatus.ARCHIVED) {
            throw new WishlistArchivedException(wishlistId);
        }
        Wish wish = new Wish(title, description, url, wishlist);
        return wishRepository.save(wish);
    }

    @Transactional(readOnly = true)
    public WishlistDetailsResponse getWishlist(Long wishlistId, long viewerId) {
        Wishlist wishlist = findById(wishlistId);
        User owner = wishlist.getOwner();
        boolean isOwner = owner.getId().equals(viewerId);
        if (!isOwner && wishlist.getStatus() == WishlistStatus.ARCHIVED) {
            throw new WishlistNotFoundException(wishlistId);
        }
        List<Wish> wishes = wishRepository.findAllByWishlistId(wishlistId);
        List<WishDetailsResponse> wishDetailsResponses = wishes.stream()
                .map(wish -> {
                    Boolean reserved = null;
                    if (!isOwner) {
                        reserved = reservationRepository.existsByWishId(wish.getId());
                    }
                    return new WishDetailsResponse(
                            wish.getId(),
                            wish.getTitle(),
                            wish.getDescription(),
                            wish.getUrl(),
                            wish.getStatus(),
                            reserved
                    );
                })
                .toList();
        return new WishlistDetailsResponse(
                wishlistId,
                wishlist.getTitle(),
                wishlist.getDescription(),
                wishlist.getStatus(),
                wishDetailsResponses
        );
    }

    @Transactional
    public void editWishlist(Long wishlistId, String newTitle, String newDescription, Long userId) {
        Wishlist wishlist = findById(wishlistId);
        checkOwner(wishlist, userId);
        if (wishlist.getStatus() == WishlistStatus.ARCHIVED) {
            throw new WishlistArchivedException(wishlistId);
        }
        wishlist.edit(newTitle, newDescription);
    }

    @Transactional
    public void deleteWishlist(Long wishlistId, Long userId) {
        Wishlist wishlist = findById(wishlistId);
        checkOwner(wishlist, userId);
        reservationRepository.deleteAllByWishWishlistId(wishlistId);
        wishRepository.deleteAllByWishlistId(wishlistId);
        wishlistRepository.delete(wishlist);
    }

    @Transactional
    public void archiveWishlist(Long wishlistId, Long userId) {
        Wishlist wishlist = findById(wishlistId);
        checkOwner(wishlist, userId);
        if (wishlist.getStatus() == WishlistStatus.ARCHIVED) {
            throw new WishlistArchivedException(wishlistId);
        }
        wishlist.archive();
        reservationRepository.deleteAllByWishWishlistId(wishlistId);
    }

    @Transactional
    public void unarchiveWishlist(Long wishlistId, Long userId) {
        Wishlist wishlist = findById(wishlistId);
        checkOwner(wishlist, userId);
        wishlist.unarchive();
    }

    @Transactional(readOnly = true)
    public List<WishlistSummaryResponse> getCurrentUserWishlists(Long userId, WishlistStatus status) {
        List<Wishlist> wishlists;
        if (status == null) {
            wishlists = wishlistRepository.findAllByOwnerId(userId);
        } else {
            wishlists = wishlistRepository.findAllByOwnerIdAndStatus(userId, status);
        }
        return wishlists.stream()
                .map(wishlist -> new WishlistSummaryResponse(wishlist.getId(), wishlist.getTitle(), wishlist.getDescription(), wishlist.getStatus()))
                .toList();
    }

    private void checkOwner(Wishlist wishlist, Long userId) {
        if (!wishlist.getOwner().getId().equals(userId)) {
            throw new WishlistAccessDeniedException(wishlist.getId(), userId);
        }
    }

}
