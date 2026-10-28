package com.dev.foryourwishes.wishlist.entity;

import com.dev.foryourwishes.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservations_seq")
    @SequenceGenerator(
            name = "reservations_seq",
            sequenceName = "users_seq",
            allocationSize = 50
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wish_id", nullable = false, unique = true)
    private Wish wish;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserved_by_id", nullable = false)
    private User reservedBy;

    public Reservation(Wish wish, User reservedBy) {
        this.wish = wish;
        this.reservedBy = reservedBy;
    }
}
