package com.dev.foryourwishes.wishlist.entity;

import com.dev.foryourwishes.user.User;
import com.dev.foryourwishes.wishlist.WishlistStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "wishlists_seq"
    )
    @SequenceGenerator(
            name = "wishlists_seq",
            sequenceName = "wishlists_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WishlistStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public void archive() {
        this.status = WishlistStatus.ARCHIVED;
    }

    public void unarchive() {
        this.status = WishlistStatus.ACTIVE;
    }

    public Wishlist(String title, String description, User owner) {
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.status = WishlistStatus.ACTIVE;
    }

    public void edit(String newTitle, String newDescription) {
        this.title = newTitle;
        this.description = newDescription;
    }

}
