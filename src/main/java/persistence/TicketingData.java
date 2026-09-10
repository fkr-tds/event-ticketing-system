package persistence;

import java.util.List;

import entities.Event;
import entities.Reservation;
import entities.Seat;
import entities.Venue;

public record TicketingData(
    List<Venue> venues,
    List<Event> events,
    List<Seat> seats,
    List<Reservation> reservations
) {}