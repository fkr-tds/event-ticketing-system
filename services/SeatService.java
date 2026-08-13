package services;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import entities.Seat;
import entities.Venue;
import repositories.TicketingRepository;

public class SeatService {

    static void addSeats(UUID venueId, String venueName, Scanner scan) {
        int totalSeatsAdded = 0;
        boolean addingSections = false;

        do {
            System.out.print("\nEnter section name (e.g., VIP, Regular, Balcony): ");
            String section = scan.next().toUpperCase();

            if ( section == null || section.isBlank()) {
                System.out.println("Section name cannot be empty.");
                continue;
            }

            System.out.print("\nEnter number of rows for section '" + section + "': ");

            if (!scan.hasNextInt()) {
                System.out.println("\nPlease enter a valid integer.");
                continue;
            }

            int numberOfRows = scan.nextInt();

            if (numberOfRows <= 0) {
                System.out.println("Number of seats must be a positive integer.");
                continue;
            }

            System.out.print("\nEnter number of seats per row for section '" + section + "': ");
            
            if (!scan.hasNextInt()) {
                System.out.println("\nPlease enter a valid integer.");
                continue;
            }
            
            int numberOfSeats = scan.nextInt();

            if (numberOfSeats <= 0) {
                System.out.println("Number of seats must be a positive integer.");
                continue;
            }

            int sectionSeatsAdded = 0;

            for (int i = 1; i <= numberOfRows; i++) {
                for (int j = 1; j <= numberOfSeats; j++) {
                    TicketingRepository.seats.add(new Seat(UUID.randomUUID(), venueId, section, String.valueOf(i), j, null));
                    sectionSeatsAdded++;
                }
            }

            totalSeatsAdded += sectionSeatsAdded;
            System.out.println("\n--> Added " + sectionSeatsAdded + " seat(s) to section '" + section + "'.");

            System.out.print("\nDo you want to add another section to this venue? (Y/N): ");
            char response = scan.next().charAt(0);
            if (response == 'y' || response == 'Y') {
                addingSections = true;
            } else if (response == 'n' || response == 'N') {
                addingSections = false;
            } else {
                System.out.println("\nInvalid input. Please enter 'Y' for Yes or 'N' for No.");
            }
        } while (addingSections);

        System.out.println("\nAdded a total of " + totalSeatsAdded + " seat(s) to venue " + venueName + ".");
    }

    public static void addSeats(Scanner scan) {
        VenueService.listVenues();
        System.out.print("\nEnter a venue Id for the seats: ");
        String venueId = scan.next();

        Venue venueToAddSeats = null;

        for(Venue venue : TicketingRepository.venues) {
            if(venue.id().toString().equals(venueId)) {
                venueToAddSeats = venue;
                break;
            }
        }

        if(venueToAddSeats == null) {
            System.out.println("\nVenue with ID " + venueId + " does not exist.");
            return;
        }

        for(Seat seat : TicketingRepository.seats) {
            if(seat.venueId().toString().equals(venueId)) {
                System.out.println("\nVenue with ID " + venueId + " already have seats.");
                return;
            }
        }

        addSeats(venueToAddSeats.id(), venueToAddSeats.name(), scan);
    }

    public static void listSeats(Scanner scan) {
        VenueService.listVenues();
        System.out.print("\nEnter the venue Id: ");
        String venueId = scan.next();

        Venue venueToListSeats = null;

        for (Venue venue : TicketingRepository.venues) {
            if (venue.id().toString().equals(venueId)) {
                venueToListSeats = venue;
                break;
            }
        }

        if (venueToListSeats == null) {
            System.out.println("\nVenue with ID " + venueId + " does not exist.");
            return;
        }

        ArrayList<Seat> seatsInTheVenue = new ArrayList<>();

        for(Seat seat : TicketingRepository.seats) {
            if(seat.venueId().toString().equals(venueId)) {
                seatsInTheVenue.add(seat);
            }
        }

        if (seatsInTheVenue.isEmpty()) {
            System.out.println("\nVenue with ID " + venueId + " does not have any seats.");
            return;
        }

        System.out.println("\nList of Seats:\n");

        for(Seat seat : TicketingRepository.seats) {
                System.out.println(seat.id() + "\t\t" + seat.section() + "\t\t" + seat.row() + "   " + seat.number());
        }
    }
}
