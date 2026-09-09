import repositories.TicketingRepository;

import entities.Event;
import entities.Reservation;
import entities.ReservationSeat;
import entities.Seat;
import entities.Venue;

import enums.EventStatus;
import enums.ReservationStatus;

import rules.PricingRules;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class RepositoryPersistenceTest {

    public static void main(String[] args) {

        // -----------------------------------
        // 1. Create repository
        // -----------------------------------

        TicketingRepository repository = new TicketingRepository();

        // -----------------------------------
        // 2. Create and add data
        // -----------------------------------

        Venue venue = new Venue(
                UUID.randomUUID(),
                "Cinema Hall",
                "Addis Ababa",
                ZoneId.of("Africa/Addis_Ababa")
        );

        repository.addVenue(venue);

        Event event = new Event(
                UUID.randomUUID(),
                venue.id(),
                "Avengers",
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(2),
                EventStatus.UPCOMING,
                new PricingRules()
        );

        repository.addEvent(event);

        Seat seat = new Seat(
                UUID.randomUUID(),
                venue.id(),
                "VIP",
                "A",
                1,
                Map.of("wheelchairAccessible", false)
        );

        repository.addSeat(seat);

        Reservation reservation = new Reservation(
                UUID.randomUUID(),
                event.id(),
                "john.doe@example.com",
                ReservationStatus.CONFIRMED,
                Instant.now().minusSeconds(86400),
                Instant.now(),
                null,
                new ArrayList<>()
        );

        ReservationSeat reservationSeat = new ReservationSeat(
                reservation.id(),
                seat.id(),
                null,
                null
        );

        reservation.items().add(reservationSeat);

        repository.addReservation(reservation);

        System.out.println("=== BEFORE SAVE ===");
        System.out.println("Venues: " + repository.findAllVenues());
        System.out.println("Events: " + repository.findAllEvents());
        System.out.println("Seats: " + repository.findAllSeats());
        System.out.println("Reservations: " + repository.findAllReservations());

        // -----------------------------------
        // 3. Save
        // -----------------------------------

        repository.save();

        System.out.println("\nData saved successfully.");

        // -----------------------------------
        // 4. Create a NEW repository
        // -----------------------------------

        TicketingRepository loadedRepository = new TicketingRepository();

        System.out.println("\n=== NEW REPOSITORY BEFORE LOAD ===");
        System.out.println("Venues: " + loadedRepository.findAllVenues());
        System.out.println("Events: " + loadedRepository.findAllEvents());
        System.out.println("Seats: " + loadedRepository.findAllSeats());
        System.out.println("Reservations: " + loadedRepository.findAllReservations());

        // -----------------------------------
        // 5. Load
        // -----------------------------------

        loadedRepository.load();

        // -----------------------------------
        // 6. Verify loaded data
        // -----------------------------------

        System.out.println("\n=== AFTER LOAD ===");
        System.out.println("Venues: " + loadedRepository.findAllVenues());
        System.out.println("Events: " + loadedRepository.findAllEvents());
        System.out.println("Seats: " + loadedRepository.findAllSeats());
        System.out.println("Reservations: " + loadedRepository.findAllReservations());

        // -----------------------------------
        // 7. Basic verification
        // -----------------------------------

        if (loadedRepository.findAllVenues().size() != 1) {
            throw new AssertionError("Venue was not loaded correctly.");
        }

        if (loadedRepository.findAllEvents().size() != 1) {
            throw new AssertionError("Event was not loaded correctly.");
        }

        if (loadedRepository.findAllSeats().size() != 1) {
            throw new AssertionError("Seat was not loaded correctly.");
        }

        if (loadedRepository.findAllReservations().size() != 1) {
            throw new AssertionError("Reservation was not loaded correctly.");
        }

        Venue loadedVenue = loadedRepository.findVenueById(venue.id());

        if (loadedVenue == null) {
            throw new AssertionError("Venue ID was not preserved.");
        }

        Event loadedEvent = loadedRepository.findEventById(event.id());

        if (loadedEvent == null) {
            throw new AssertionError("Event ID was not preserved.");
        }

        Reservation loadedReservation =
                loadedRepository.findReservationById(reservation.id());

        if (loadedReservation == null) {
            throw new AssertionError("Reservation ID was not preserved.");
        }

        if (loadedReservation.items().size() != 1) {
            throw new AssertionError("Reservation seat was not loaded correctly.");
        }

        System.out.println("\n=================================");
        System.out.println("PERSISTENCE TEST PASSED");
        System.out.println("=================================");
    }
}