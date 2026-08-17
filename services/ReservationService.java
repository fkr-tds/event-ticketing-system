package services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import entities.Event;
import entities.Reservation;
import entities.ReservationSeat;
import entities.Seat;
import enums.ReservationStatus;
import repositories.TicketingRepository;

public class ReservationService {

    private final Scanner scan;
    private final TicketingRepository ticketingRepository;

    public ReservationService(Scanner scan, TicketingRepository ticketingRepository) {
        this.scan = scan;
        this.ticketingRepository = ticketingRepository;
    }

    public void makeReservation() {
        System.out.print("\nEnter the event Id for which you want to make a reservation: ");
        UUID eventId;
        try {
            eventId = UUID.fromString(scan.next());
            scan.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format. Please enter a valid event ID.");
            return;
        }

        Event eventToReserve = null;

        for (Event event : ticketingRepository.findAllEvents()) {
            if (eventId.equals(event.id())) {
                eventToReserve = event;
                break;
            }
        }

        if (eventToReserve == null) {
            System.out.println("Event with ID " + eventId + " does not exist.");
            return;
        }

        List<Reservation> reservationsForEvent = new ArrayList<>();

        for (Reservation reservation : ticketingRepository.findAllReservations()) {
            if (reservation.eventId().equals(eventId)) {
                reservationsForEvent.add(reservation);
            }
        }

        List<ReservationSeat> reservedSeatsForEvent = new ArrayList<>();

        for(Reservation reservation: reservationsForEvent) {
            reservedSeatsForEvent.addAll(reservation.items());
        }

        List<Seat> seatsInTheVenue = new ArrayList<>();

        for(Seat seat : ticketingRepository.findAllSeats()) {
            if(seat.venueId().equals(eventToReserve.venueId())) {
                seatsInTheVenue.add(seat);
            }
        }

        List<Seat> availableSeats = new ArrayList<>(seatsInTheVenue);

        for (Seat seat : seatsInTheVenue) {
            for (ReservationSeat reservationSeat : reservedSeatsForEvent) {
                if (reservationSeat.seatId().equals(seat.id())) {
                    availableSeats.remove(seat);
                    break;
                }
            }
        }

        if (availableSeats.isEmpty()) {
            System.out.println("No available seats for the event with ID " + eventId + ".");
            return;
        }

        Map<String, Integer> availableSeatsPerSection = new HashMap<>();

        for (Seat seat : availableSeats) {
                int count = availableSeatsPerSection.getOrDefault(seat.section(), 0);
                availableSeatsPerSection.put(seat.section(), count + 1);
        }

        System.out.println("\nAvailable seat sections: " + availableSeatsPerSection.keySet());
        System.out.print("\nEnter the seat section you want to reserve: ");
        String selectedSection = scan.next().toUpperCase();
        scan.nextLine();

        if(!availableSeatsPerSection.containsKey(selectedSection)) {
            System.out.println("\nInvalid section. Please select a valid section.");
            return;
        }

        System.out.println("\nAvailable seats in section " + selectedSection + ": " + availableSeatsPerSection.get(selectedSection));
        System.out.print("\nEnter the number of seats you want to reserve in section " + selectedSection + ": ");

        if (!scan.hasNextInt()) {
            System.out.println("\nPlease enter a valid integer.");
            return;
        }

        int numberOfSeatsToReserve = scan.nextInt();
        scan.nextLine();

        if (numberOfSeatsToReserve <= 0 || numberOfSeatsToReserve > availableSeatsPerSection.get(selectedSection)) {
            System.out.println("\nInvalid number of seats. Please enter a valid number.");
            return;
        }

        System.out.print("\nEnter your email address: ");
        String customerEmail = scan.nextLine();

        List<Seat> holdSeats = new ArrayList<>();

        for (Seat seat : availableSeats) {
            if (seat.section().equals(selectedSection)) {
                holdSeats.add(seat);
                
                if (holdSeats.size() == numberOfSeatsToReserve) {
                    break;
                }
            }
        }

        UUID reservationId = UUID.randomUUID();

        List<ReservationSeat> reservationSeats = new ArrayList<>();

        for (Seat seat : holdSeats) {
            reservationSeats.add(new ReservationSeat(reservationId, seat.id(), null, null));
        }

        Instant createdAt = Instant.now();

        ticketingRepository.addReservation(new Reservation(reservationId, eventId, customerEmail, ReservationStatus.HOLD, createdAt, null, createdAt.plus(1, ChronoUnit.MINUTES), reservationSeats));

        System.out.print("\nSuccessfully Reserved!!!");
    }
}