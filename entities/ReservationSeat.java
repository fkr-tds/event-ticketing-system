package entities;

import java.util.UUID;

import valueobjects.Money;

public record ReservationSeat (
    UUID id,
    UUID seatId,
    Money price,
    String discountCode
) {}