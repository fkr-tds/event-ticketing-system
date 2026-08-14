package repositories;

import java.util.ArrayList;
import java.util.List;

import entities.Event;
import entities.Seat;
import entities.Venue;

public class TicketingRepository {
    private final List<Venue> venues = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    private final List<Seat> seats = new ArrayList<>();

    public void addVenue(Venue venue) {
        venues.add(venue);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public List<Venue> listVenues() {
        return List.copyOf(venues);
    }

    public List<Event> listEvents() {
        return List.copyOf(events);
    }

    public List<Seat> listSeats() {
        return List.copyOf(seats);
    }
}
