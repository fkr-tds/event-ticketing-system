package repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entities.Event;
import entities.Reservation;
import entities.Seat;
import entities.Venue;

public class TicketingRepository {
    private final List<Venue> venues = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    private final List<Seat> seats = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

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

    public List<Venue> findAllVenues() {
        return List.copyOf(venues);
    }

    public Venue findVenueById(UUID venueId) {
        for (Venue venue : venues) {
            if (venue.id().equals(venueId)) {
                return venue;
            }
        }

        return null;
    }

    public List<Event> findAllEvents() {
        return List.copyOf(events);
    }

    public Event findEventById(UUID eventId) {
        for (Event event : events) {
            if (event.id().equals(eventId)) {
                return event;
            }
        }

        return null;
    }

    public List<Seat> findAllSeats() {
        return List.copyOf(seats);
    }

    public List<Seat> findSeatsByVenueId(UUID venueId) {

        ArrayList<Seat> seatsInTheVenue = new ArrayList<>();

        for (Seat seat : seats) {
            if (seat.venueId().equals(venueId)) {
                seatsInTheVenue.add(seat);
            }
        }

        return List.copyOf(seatsInTheVenue);
    }

    public List<Reservation> findAllReservations() {
        return List.copyOf(reservations);
    }

    public Reservation findReservationById(UUID reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.id().equals(reservationId)) {
                return reservation;
            }
        }

        return null;
    }

    public void updateReservation(Reservation updatedResrvation) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).id().equals(updatedResrvation.id())) {
                reservations.set(i, updatedResrvation);
                return;
            }
        }
    }
}
