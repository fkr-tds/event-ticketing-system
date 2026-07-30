package entities;

import java.util.UUID;

import valueobjects.Money;

public class ReservationSeat {
    UUID id;
    UUID seatId;
    Money price;
    String discountCode;
}