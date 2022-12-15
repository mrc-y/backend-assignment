CREATE SEQUENCE bookings_seq;

CREATE TYPE booking_status AS ENUM ('in_progress','failed','successful');

CREATE TABLE bookings
(
    id               bigint check (id > 0) NOT NULL DEFAULT NEXTVAL('hotels_seq'),
    name             text                  NOT NULL,
    surname          text                           DEFAULT '',
    birthdate        date                           DEFAULT NULL,
    hotel_id         bigint                NOT NULL,
    room_id          bigint                NOT NULL,
    price_before_tax decimal               NOT NULL,
    price_after_tax  decimal               NOT NULL,
    currency         character(3)          NOT NULL,
    check_in         date                  NOT NULL,
    check_out        date                  NOT NULL,
    booking_status   booking_status        NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT bookings_hotel_id_foreign FOREIGN KEY (hotel_id) REFERENCES hotels (id) ON DELETE CASCADE,
    CONSTRAINT bookings_room_id_foreign FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE
);