package com.dev.foryourwishes.wishlist.entity;

import com.dev.foryourwishes.wishlist.WishStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wish {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "wishes_seq"
    )
    @SequenceGenerator(
            name = "wishes_seq",
            sequenceName = "wishes_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    private String description;
    private String url;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WishStatus status;

    public Wish(String title, String description, String url, Wishlist wishlist) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.wishlist = wishlist;
        this.status = WishStatus.ACTIVE;
    }

    public void markAsFulfilled() {
        this.status = WishStatus.FULFILLED;
    }

    public void editWish(String newTitle, String newDescription, String newUrl) {
        this.title = newTitle;
        this.description = newDescription;
        this.url = newUrl;
    }
}
