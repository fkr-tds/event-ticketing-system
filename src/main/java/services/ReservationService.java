package services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
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

        Event eventToReserve = ticketingRepository.findEventById(eventId);

        if (eventToReserve == null) {
            System.out.println("Event with ID " + eventId + " does not exist.");
            return;
        }

        Instant now = Instant.now();

        List<Reservation> activeReservationsForEvent = new ArrayList<>();

        for (Reservation reservation : ticketingRepository.findAllReservations()) {
            if (!reservation.eventId().equals(eventId)) {
                continue;
            }

            if ((reservation.status() == ReservationStatus.HOLD && reservation.holdExpiresAt() != null && reservation.holdExpiresAt().isAfter(now)) || reservation.status() == ReservationStatus.CONFIRMED) {
                activeReservationsForEvent.add(reservation);
            }
        }

        List<ReservationSeat> reservedSeatsForEvent = new ArrayList<>();

        for(Reservation reservation: activeReservationsForEvent) {
            reservedSeatsForEvent.addAll(reservation.items());
        }

        List<Seat> seatsInTheVenue = ticketingRepository.findSeatsByVenueId(eventToReserve.venueId());

        Set<UUID> reservedSeatIds = new HashSet<>();

        for (ReservationSeat reservationSeat : reservedSeatsForEvent) {
            reservedSeatIds.add(reservationSeat.seatId());
        }

        List<Seat> availableSeats = new ArrayList<>();

        for (Seat seat : seatsInTheVenue) {
            if (!reservedSeatIds.contains(seat.id())) {
                availableSeats.add(seat);
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

        List<Seat> selectedSeats = new ArrayList<>();

        for (Seat seat : availableSeats) {
            if (seat.section().equals(selectedSection)) {
                selectedSeats.add(seat);
                
                if (selectedSeats.size() == numberOfSeatsToReserve) {
                    break;
                }
            }
        }

        UUID reservationId = UUID.randomUUID();

        List<ReservationSeat> reservationSeats = new ArrayList<>();

        for (Seat seat : selectedSeats) {
            reservationSeats.add(new ReservationSeat(reservationId, seat.id(), null, null));
        }

        Instant holdExpiresAt = now.plus(5, ChronoUnit.MINUTES);

        ticketingRepository.addReservation(new Reservation(reservationId, eventId, customerEmail, ReservationStatus.HOLD, now, null, holdExpiresAt, reservationSeats));

        System.out.print("\nReservation made successfully! Your reservation ID is: " + reservationId);
        System.out.println("\nPlease confirm your reservation within 5 minutes.");
    }

    public void confirmReservation() {
        System.out.print("\nEnter the reservation ID you want to confirm: ");
        UUID reservationId;
        try {
            reservationId = UUID.fromString(scan.next());
            scan.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format. Please enter a valid reservation ID.");
            return;
        }

        Reservation reservationToConfirm = ticketingRepository.findReservationById(reservationId);

        if (reservationToConfirm == null) {
            System.out.println("Reservation not found.");
            return;
        }

        if (reservationToConfirm.status() != ReservationStatus.HOLD) {
            System.out.println("Reservation is not in HOLD status and cannot be confirmed.");
            return;
        }

        Instant now = Instant.now();

        if (reservationToConfirm.holdExpiresAt() != null && !reservationToConfirm.holdExpiresAt().isAfter(now)) {
            System.out.println("Reservation hold has expired and cannot be confirmed.");
            return;
        }

        ticketingRepository.updateReservation(new Reservation(reservationToConfirm.id(), reservationToConfirm.eventId(), reservationToConfirm.customerEmail(), ReservationStatus.CONFIRMED, reservationToConfirm.createdAt(), now, null, reservationToConfirm.items()));

        System.out.println("Reservation confirmed successfully!");
    }

    public void cancelReservation() {
        System.out.print("\nEnter the reservation ID you want to cancel: ");
        UUID reservationId;
        try {
            reservationId = UUID.fromString(scan.next());
            scan.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format. Please enter a valid reservation ID.");
            return;
        }

        Reservation reservationToCancel = ticketingRepository.findReservationById(reservationId);

        if (reservationToCancel == null) {
            System.out.println("Reservation not found.");
            return;
        }

        if (reservationToCancel.status() != ReservationStatus.HOLD) {
            System.out.println("Reservation is not in HOLD status and cannot be cancelled.");
            return;
        }

        Instant now = Instant.now();

        if (reservationToCancel.holdExpiresAt() != null && !reservationToCancel.holdExpiresAt().isAfter(now)) {
            System.out.println("Reservation hold has already expired and cannot be cancelled.");
            return;
        }

        ticketingRepository.updateReservation(new Reservation(reservationToCancel.id(), reservationToCancel.eventId(), reservationToCancel.customerEmail(), ReservationStatus.CANCELLED, reservationToCancel.createdAt(), now, null, reservationToCancel.items()));

        System.out.println("Reservation cancelled successfully!");    }
}