package services;

import java.time.ZoneId;
import java.util.Scanner;
import java.util.UUID;

import entities.Venue;
import repositories.TicketingRepository;

public class VenueService {

    private final Scanner scan;
    private final SeatService seatService;
    private final TicketingRepository ticketingRepository;

    public VenueService (Scanner scan, SeatService seatService, TicketingRepository ticketingRepository) {
        this.scan = scan;
        this.seatService = seatService;
        this.ticketingRepository = ticketingRepository;
    }
    
    public void addVenue() {
        System.out.print("\nEnter the venue name: ");
        String venueName = scan.nextLine().trim();

        System.out.print("\nEnter the address of "+ venueName + ": ");
        String venueAddress = scan.nextLine().trim();

        Venue venue = new Venue(UUID.randomUUID(), venueName, venueAddress, ZoneId.of("Africa/Addis_Ababa"));
        ticketingRepository.addVenue(venue);

        System.out.print("\nWould you you like to add seats to " + venueName + "? (Y/N): ");
        char addSeatsChoice = scan.next().charAt(0);
        scan.nextLine();

        if (addSeatsChoice == 'Y' || addSeatsChoice == 'y') {
            seatService.addSeats(venue.id(), venue.name());
        } else if (addSeatsChoice == 'N' || addSeatsChoice == 'n') {
            System.out.println("\nYou have chosen not to add seats to " + venueName + ".");
        } else {
            System.out.println("\nInvalid input. Please enter 'Y' for Yes or 'N' for No.");
        }

        System.out.println("\nVenue added successfully.");
    }

    public void listVenues() {
        System.out.println("\nList of Venues:");
        for (Venue venue : ticketingRepository.findAllVenues()) {
            System.out.println("\n-----------------------------");
            System.out.println("\nVenue ID: " + venue.id());
            System.out.println("Venue Name: " + venue.name());
            System.out.println("Venue Address: " + venue.address());
            System.out.println("Venue Time Zone: " + venue.timezone());
            System.out.println("\n-----------------------------");
        }
    }
}
