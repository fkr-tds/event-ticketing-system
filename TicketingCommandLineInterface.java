import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import entities.Event;
import entities.Seat;
import entities.Venue;
import enums.EventStatus;

public class TicketingCommandLineInterface {

    static Scanner scan = new Scanner(System.in);

    static ArrayList<Venue> venues = new ArrayList<>();
    static ArrayList<Event> events = new ArrayList<>();
    static ArrayList<Seat> seats = new ArrayList<>();

    public static void main(String[] args) {

        boolean restartTicketingSystem = true;

        do {
            System.out.println("\n\n\n// = = = = = = = = = = Welcome to Atlas Ticketing System = = = = = = = = = = //\n");
            System.out.print("\nAre you Operator or Customer? (Enter 'O' for Operator, 'C' for Customer): ");

            char userType = scan.next().charAt(0);

            if (userType == 'O' || userType == 'o') {
                System.out.println("\nYou are logged in as an Operator. You can add a Venue, an Event or Seats.");
                System.out.println("\n1. Add Venue\t2. Add Event\t3. Add Seats");
                System.out.print("\nPlease select an option (1, 2 or 3): ");

                if (!scan.hasNextInt()) {
                    System.out.println("\nPlease enter a valid integer.");
                    continue;
                }
                
                int operatorChoice = scan.nextInt();

                switch (operatorChoice) {
                    case 1:
                        addVenue();
                        break;
                    case 2:
                        addEvent();
                        break;
                    case 3:
                        addSeats();
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please select a valid option.");
                }
            } else if (userType == 'C' || userType == 'c') {
                System.out.println("\nYou are logged in as a Customer. What would you like to do?");
                System.out.println("\n1. List Venues\t2. List Events");
                System.out.print("\nPlease select an option (1 or 2): ");

                if (!scan.hasNextInt()) {
                    System.out.println("\nPlease enter a valid integer.");
                    continue;
                }

                int customerChoice = scan.nextInt();

                switch (customerChoice) {
                    case 1:
                        listVenues();
                        break;
                    case 2:
                        listEvents();
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please select a valid option.");
                }
            } else {
                System.out.println("\nInvalid input. Please enter 'O' for Operator or 'C' for Customer.");
            }
        } while(restartTicketingSystem);

        scan.close();
    }

    static void addVenue() {
        System.out.print("\nEnter the venue name: ");
        String venueName = scan.next();

        System.out.print("\nEnter the address of "+ venueName + ": ");
        String venueAddress = scan.next();

        Venue venue = new Venue(UUID.randomUUID(), venueName, venueAddress, ZoneId.of("Africa/Addis_Ababa"));
        venues.add(venue);

        System.out.print("Would you you like to add seats to " + venueName + "? (Y/N): ");
        char addSeatsChoice = scan.next().charAt(0);

        if (addSeatsChoice == 'Y' || addSeatsChoice == 'y') {
            addSeats(venue.id(), venue.name());
        } else if (addSeatsChoice == 'N' || addSeatsChoice == 'n') {
            System.out.println("\nYou have chosen not to add seats to " + venueName + ".");
        } else {
            System.out.println("\nInvalid input. Please enter 'Y' for Yes or 'N' for No.");
        }

        System.out.println("\nVenue added successfully.");
    }

    static void addEvent() {
        System.out.print("Enter a venue Id for the event: ");
        String venueId = scan.next();
        for(Venue venue : venues) {
            if(!venue.id().toString().equals(venueId)) {
                System.out.println("\nVenue with ID " + venueId + " does not exist.");
                return;
            }
        }
        for(Seat seat : seats) {
            if(!seat.venueId().toString().equals(venueId)) {
                System.out.println("\nVenue with ID " + venueId + " does not have any seats.");
                return;
            }
        }
        System.out.print("\nEnter the event title: ");
        String eventTitle = scan.next();
        System.out.print("Date [yyyy-MM-dd] (eg. 2027-01-01): ");
        String dateString = scan.nextLine();
        LocalDate date = LocalDate.parse(dateString);
        if(!dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("\nInvalid date format. Please enter the date in the format yyyy-MM-dd.");
            return;
        }
        if(date.isBefore(LocalDate.now())) {
            System.out.println("\nInvalid date. Please enter a future date.");
            return;
        }
        System.out.print("Time [HH:mm] (eg. 01:01): ");
        String timeString = scan.nextLine();
        LocalTime time = LocalTime.parse(timeString);
        if(!timeString.matches("\\d{2}:\\d{2}")) {
            System.out.println("\nInvalid time format. Please enter the time in the format HH:mm.");
            return;
        }
        LocalDateTime startLocalDateTime = LocalDateTime.of(date, time);
        ZoneId venueTimezone = null;
        for(Venue venue : venues) {
            if(venue.id().toString().equals(venueId)) {
                venueTimezone = venue.timezone();
                break;
            }
        }
        ZonedDateTime startZonedDateTime = startLocalDateTime.atZone(venueTimezone);
        System.out.print("Enter the event duration in minutes: ");
        int duration = scan.nextInt();
        LocalDateTime endLocalDateTime = startLocalDateTime.plusMinutes(duration);
        ZonedDateTime endZonedDateTime = endLocalDateTime.atZone(venueTimezone);
        events.add(new Event(UUID.randomUUID(), UUID.fromString(venueId), eventTitle, startZonedDateTime, endZonedDateTime, EventStatus.UPCOMING, null));
    }

    static void addSeats(UUID venueId, String venueName) {
        int totalSeatsAdded = 0;
        boolean addingSections = false;

        do {
            System.out.print("\nEnter section name (e.g., VIP, Regular, Balcony): ");
            String section = scan.next();

            if (section.isBlank() || section == null) {
                System.out.println("Section name cannot be empty.");
                continue;
            }

            System.out.print("Enter number of rows for section '" + section + "': ");

            if (!scan.hasNextInt()) {
                System.out.println("\nPlease enter a valid integer.");
                continue;
            }

            int numberOfRows = scan.nextInt();

            System.out.print("Enter number of seats per row for section '" + section + "': ");
            
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
                    seats.add(new Seat(UUID.randomUUID(), venueId, section, String.valueOf(i), j, null));
                    sectionSeatsAdded++;
                }
            }

            totalSeatsAdded += sectionSeatsAdded;
            System.out.println("--> Added " + sectionSeatsAdded + " seat(s) to section '" + section + "'.");

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

    static void addSeats() {
        System.out.print("Enter a venue Id for the seats: ");
        String venueId = scan.next();
        Venue venueToAddSeats = null;
        for(Venue venue : venues) {
            if(venue.id().toString().equals(venueId)) {
                venueToAddSeats = venue;
                break;
            }
        }

        if(venueToAddSeats == null) {
            System.out.println("\nVenue with ID " + venueId + " does not exist.");
            return;
        }

        for(Seat seat : seats) {
            if(seat.venueId().toString().equals(venueId)) {
                System.out.println("\nVenue with ID " + venueId + " already have seats.");
                return;
            }
        }

        int totalSeatsAdded = 0;
        boolean addingSections = false;

        do {
            System.out.print("\nEnter section name (e.g., VIP, Regular, Balcony): ");
            String section = scan.next();

            if (section.isBlank() || section == null) {
                System.out.println("Section name cannot be empty.");
                continue;
            }

            System.out.print("Enter number of rows for section '" + section + "': ");

            if (!scan.hasNextInt()) {
                System.out.println("\nPlease enter a valid integer.");
                continue;
            }
            
            int numberOfRows = scan.nextInt();

            System.out.print("Enter number of seats per row for section '" + section + "': ");

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
                    seats.add(new Seat(UUID.randomUUID(), venueToAddSeats.id(), section, String.valueOf(i), j, null));
                    sectionSeatsAdded++;
                }
            }

            totalSeatsAdded += sectionSeatsAdded;
            System.out.println("--> Added " + sectionSeatsAdded + " seat(s) to section '" + section + "'.");

            System.out.print("\nDo you want to add another section to this venue? (Y/N): ");
            char addSectionChoice = scan.next().charAt(0);
            if (addSectionChoice == 'y' || addSectionChoice == 'Y') {
                addingSections = true;
            } else if (addSectionChoice == 'n' || addSectionChoice == 'N') {
                addingSections = false;
            } else {
                System.out.println("\nInvalid input. Please enter 'Y' for Yes or 'N' for No.");
            }
            } while (addingSections);

        System.out.println("\nAdded a total of " + totalSeatsAdded + " seat(s) to venue " + venueToAddSeats.name() + ".");
    }

    static void listVenues() {
        System.out.println("\nList of Venues:");
        for (Venue venue : venues) {
            System.out.println("\n-----------------------------");
            System.out.println("\nVenue ID: " + venue.id());
            System.out.println("Venue Name: " + venue.name());
            System.out.println("Venue Address: " + venue.address());
            System.out.println("Venue Time Zone: " + venue.timezone());
        }
    }

    static void listEvents() {
        System.out.println("\nList of Events:");
        for (Event event : events) {
            System.out.println("\n-----------------------------");
            System.out.println("\nEvent ID: " + event.id());
            System.out.println("Venue ID: " + event.venueId());
            System.out.println("Event Title: " + event.title());
            System.out.println("Event Start Time: " + event.start());
            System.out.println("Event End Time: " + event.end());
            System.out.println("Event Status: " + event.status());
        }
    }
}
