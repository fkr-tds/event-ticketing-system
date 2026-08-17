package repositories;

import java.util.ArrayList;
import java.util.List;

import entities.Event;
import entities.Reservation;
import entities.ReservationSeat;
import entities.Seat;
import entities.Venue;

public class TicketingRepository {
    private final List<Venue> venues = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    private final List<Seat> seats = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<ReservationSeat> reservationSeats = new ArrayList<>();

    public void addVenue(Venue venue) {
        venues.add(venue);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void addReservationSeat(ReservationSeat reservationSeat) {
        reservationSeats.add(reservationSeat);
    }

    public List<Venue> findAllVenues() {
        return List.copyOf(venues);
    }

    public List<Event> findAllEvents() {
        return List.copyOf(events);
    }

    public List<Seat> findAllSeats() {
        return List.copyOf(seats);
    }

    public List<Reservation> findAllReservations() {
        return List.copyOf(reservations);
    }

    public List<ReservationSeat> findAllReservationSeats() {
        return List.copyOf(reservationSeats);
    }
}
