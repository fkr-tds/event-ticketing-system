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
import repositories.TicketingRepository;

public class TicketingCommandLineInterface {

    static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) {

        boolean restartTicketingSystem = true;

        do {
            System.out.println("\n\n\n// = = = = = = = = = = Welcome to Atlas Ticketing System = = = = = = = = = = //\n");
            System.out.print("\nAre you Operator or Customer? (Enter 'O' for Operator, 'C' for Customer): ");

            char userType = scan.next().charAt(0);

            if (userType == 'O' || userType == 'o') {
                System.out.println("\nYou are logged in as an Operator. What would you like to do?");
                System.out.println("\n1. Add Venue\t2. List Venues\t3. Add Event\t4. List Events\t5. Add Seats\t6. List Seats");
                System.out.print("\nPlease select an option (1, 2, 3, 4, 5 or 6): ");

                if (!scan.hasNextInt()) {
                    System.out.println("\nPlease enter a valid integer.");
                    continue;
                }
                
                int operatorChoice = scan.nextInt();
                scan.nextLine();

                switch (operatorChoice) {
                    case 1:
                        addVenue();
                        break;
                    case 2:
                        listVenues();
                        break;
                    case 3:
                        addEvent();
                        break;
                    case 4:
                        listEvents();
                        break;
                    case 5:
                        addSeats();
                        break;
                    case 6:
                        listSeats();
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
        String venueName = scan.nextLine().trim();

        System.out.print("\nEnter the address of "+ venueName + ": ");
        String venueAddress = scan.nextLine().trim();

        Venue venue = new Venue(UUID.randomUUID(), venueName, venueAddress, ZoneId.of("Africa/Addis_Ababa"));
        TicketingRepository.venues.add(venue);

        System.out.print("\nWould you you like to add seats to " + venueName + "? (Y/N): ");
        char addSeatsChoice = scan.next().charAt(0);
        scan.nextLine();

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
        listVenues();
        System.out.print("\nEnter a venue Id for the event: ");
        String venueId = scan.next();
        scan.nextLine();

        Venue venueToAddEvent = null;

        for(Venue venue : TicketingRepository.venues) {
            if(venue.id().toString().equals(venueId)) {
                venueToAddEvent = venue;
                break;
            }
        }

        if (venueToAddEvent == null) {
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

        System.out.print("\nEnter the event title: ");
        String eventTitle = scan.nextLine().trim();

        System.out.print("\nDate [yyyy-MM-dd] (eg. 2027-01-01): ");
        String dateString = scan.nextLine();

        if(!dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("\nInvalid date format. Please enter the date in the format yyyy-MM-dd.");
            return;
        }

        LocalDate date = LocalDate.parse(dateString);

        if(date.isBefore(LocalDate.now())) {
            System.out.println("\nInvalid date. Please enter a future date.");
            return;
        }

        System.out.print("\nTime [HH:mm] (eg. 01:01): ");
        String timeString = scan.nextLine();

        if(!timeString.matches("\\d{2}:\\d{2}")) {
            System.out.println("\nInvalid time format. Please enter the time in the format HH:mm.");
            return;
        }

        LocalTime time = LocalTime.parse(timeString);

        LocalDateTime startLocalDateTime = LocalDateTime.of(date, time);

        ZoneId venueTimezone = null;

        for(Venue venue : TicketingRepository.venues) {
            if(venue.id().toString().equals(venueId)) {
                venueTimezone = venue.timezone();
                break;
            }
        }

        ZonedDateTime startZonedDateTime = startLocalDateTime.atZone(venueTimezone);

        System.out.print("\nEnter the event duration in minutes: ");
        int duration = scan.nextInt();
        scan.nextLine();

        LocalDateTime endLocalDateTime = startLocalDateTime.plusMinutes(duration);
        ZonedDateTime endZonedDateTime = endLocalDateTime.atZone(venueTimezone);

        TicketingRepository.events.add(new Event(UUID.randomUUID(), UUID.fromString(venueId), eventTitle, startZonedDateTime, endZonedDateTime, EventStatus.UPCOMING, null));

        System.out.println("\t Event added successfully.");
    }

    static void addSeats(UUID venueId, String venueName) {
        int totalSeatsAdded = 0;
        boolean addingSections = false;

        do {
            System.out.print("\nEnter section name (e.g., VIP, Regular, Balcony): ");
            String section = scan.next().toUpperCase();

            if (section.isBlank() || section == null) {
                System.out.println("Section name cannot be empty.");
                continue;
            }

            System.out.print("\nEnter number of rows for section '" + section + "': ");

            if (!scan.hasNextInt()) {
                System.out.println("\nPlease enter a valid integer.");
                continue;
            }

            int numberOfRows = scan.nextInt();

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

    static void addSeats() {
        listVenues();
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

        int totalSeatsAdded = 0;
        boolean addingSections = false;

        do {
            System.out.print("\nEnter section name (e.g., VIP, Regular, Balcony): ");
            String section = scan.next().toUpperCase();

            if (section.isBlank() || section == null) {
                System.out.println("Section name cannot be empty.");
                continue;
            }

            System.out.print("\nEnter number of rows for section '" + section + "': ");

            if (!scan.hasNextInt()) {
                System.out.println("\nPlease enter a valid integer.");
                continue;
            }
            
            int numberOfRows = scan.nextInt();

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
                    TicketingRepository.seats.add(new Seat(UUID.randomUUID(), venueToAddSeats.id(), section, String.valueOf(i), j, null));
                    sectionSeatsAdded++;
                }
            }

            totalSeatsAdded += sectionSeatsAdded;
            System.out.println("\n--> Added " + sectionSeatsAdded + " seat(s) to section '" + section + "'.");

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
        for (Venue venue : TicketingRepository.venues) {
            System.out.println("\n-----------------------------");
            System.out.println("\nVenue ID: " + venue.id());
            System.out.println("Venue Name: " + venue.name());
            System.out.println("Venue Address: " + venue.address());
            System.out.println("Venue Time Zone: " + venue.timezone());
            System.out.println("\n-----------------------------");
        }
    }

    static void listEvents() {
        System.out.println("\nList of Events:");
        for (Event event : TicketingRepository.events) {
            System.out.println("\n-----------------------------");
            System.out.println("\nEvent ID: " + event.id());
            System.out.println("Venue ID: " + event.venueId());
            System.out.println("Event Title: " + event.title());
            System.out.println("Event Start Time: " + event.start());
            System.out.println("Event End Time: " + event.end());
            System.out.println("Event Status: " + event.status());
            System.out.println("\n-----------------------------");
        }
    }

    static void listSeats() {
        listVenues();
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
