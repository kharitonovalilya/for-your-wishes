CREATE SEQUENCE users_seq
START WITH 1
INCREMENT BY 50;

CREATE SEQUENCE wishlists_seq
START WITH 1
INCREMENT BY 50;

CREATE SEQUENCE wishes_seq
START WITH 1
INCREMENT BY 50;

CREATE SEQUENCE reservations_seq
START WITH 1
INCREMENT BY 50;


CREATE TABLE users
(
    id            BIGINT       NOT NULL,
    email         VARCHAR(255) NOT NULL,
    login         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_login UNIQUE (login)
);


CREATE TABLE wishlists
(
    id          BIGINT       NOT NULL,
    owner_id    BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    status      VARCHAR(255) NOT NULL,

    CONSTRAINT pk_wishlists PRIMARY KEY (id),

    CONSTRAINT fk_wishlists_owner
    FOREIGN KEY (owner_id)
    REFERENCES users (id)
);


CREATE TABLE wishes
(
    id          BIGINT       NOT NULL,
    wishlist_id BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    url         VARCHAR(255),
    status      VARCHAR(255) NOT NULL,

    CONSTRAINT pk_wishes PRIMARY KEY (id),

    CONSTRAINT fk_wishes_wishlist
    FOREIGN KEY (wishlist_id)
    REFERENCES wishlists (id)
);


CREATE TABLE reservations
(
    id             BIGINT NOT NULL,
    wish_id        BIGINT NOT NULL,
    reserved_by_id BIGINT NOT NULL,

    CONSTRAINT pk_reservations PRIMARY KEY (id),
    CONSTRAINT uk_reservations_wish UNIQUE (wish_id),

    CONSTRAINT fk_reservations_wish
    FOREIGN KEY (wish_id)
    REFERENCES wishes (id),

    CONSTRAINT fk_reservations_user
    FOREIGN KEY (reserved_by_id)
    REFERENCES users (id)
);


CREATE INDEX idx_wishlists_owner_id
ON wishlists (owner_id);

CREATE INDEX idx_wishes_wishlist_id
ON wishes (wishlist_id);

CREATE INDEX idx_reservations_reserved_by_id
ON reservations (reserved_by_id);