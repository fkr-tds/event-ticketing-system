package entities;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import enums.ReservationStatus;

public class Reservation {
    UUID id;
    UUID eventId;
    String customerEmail;
    ReservationStatus status;
    Instant createdAt;
    Instant confirmedAt;
    Instant holdExpiresAt;
    List<ReservationSeat> items;
}