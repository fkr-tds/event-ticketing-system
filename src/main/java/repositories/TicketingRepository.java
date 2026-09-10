package repositories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import entities.Event;
import entities.Reservation;
import entities.Seat;
import entities.Venue;
import persistence.TicketingData;

public class TicketingRepository {
    
    private final List<Venue> venues = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    private final List<Seat> seats = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    private final File file = new File("data/app.json");

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

    public void updateReservation(Reservation updatedReservation) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).id().equals(updatedReservation.id())) {
                reservations.set(i, updatedReservation);
                return;
            }
        }
    }

    public void save() {
        try {
            TicketingData data = new TicketingData(
                List.copyOf(venues),
                List.copyOf(events),
                List.copyOf(seats),
                List.copyOf(reservations)
            );

            mapper.writeValue(file, data);   
        } catch (Exception e) {
            throw new RuntimeException("Failed to save data.", e);
        }
    }

    public void load() {
        try {
            if (!file.exists()) {
                return;
            }

            TicketingData data = mapper.readValue(file, TicketingData.class);

            venues.clear();
            events.clear();
            seats.clear();
            reservations.clear();

            venues.addAll(data.venues());
            events.addAll(data.events());
            seats.addAll(data.seats());
            reservations.addAll(data.reservations());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load data.", e);
        }
    }
}