package services;

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

public class EventService {

    private final Scanner scan;
    private final TicketingRepository ticketingRepository;

    public EventService (Scanner scan, TicketingRepository ticketingRepository) {
        this.scan = scan;
        this.ticketingRepository = ticketingRepository;
    }

    public void addEvent() {
        System.out.print("\nEnter a venue Id for the event: ");
        UUID venueId;
        try {
            venueId = UUID.fromString(scan.next());
            scan.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format. Please enter a valid venue ID.");
            return;
        }

        Venue venueToAddEvent = null;

        for(Venue venue : ticketingRepository.findAllVenues()) {
            if(venueId.equals(venue.id())) {
                venueToAddEvent = venue;
                break;
            }
        }

        if (venueToAddEvent == null) {
            System.out.println("\nVenue with ID " + venueId + " does not exist.");
            return;
        }

        ArrayList<Seat> seatsInTheVenue = new ArrayList<>();

        for(Seat seat : ticketingRepository.findAllSeats()) {
            if(venueId.equals(seat.venueId())) {
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

        for(Venue venue : ticketingRepository.findAllVenues()) {
            if(venueId.equals(venue.id())) {
                venueTimezone = venue.timezone();
                break;
            }
        }

        ZonedDateTime startZonedDateTime = startLocalDateTime.atZone(venueTimezone);

        System.out.print("\nEnter the event duration in minutes: ");

        if (!scan.hasNextInt()) {
            System.out.println("\nPlease enter a valid integer.");
            return;
        }

        int duration = scan.nextInt();
        scan.nextLine();

        if (duration <= 0) {
            System.out.println("Event duration must be a positive number of minutes.");
            return;
        }

        LocalDateTime endLocalDateTime = startLocalDateTime.plusMinutes(duration);
        ZonedDateTime endZonedDateTime = endLocalDateTime.atZone(venueTimezone);

        ticketingRepository.addEvent(new Event(UUID.randomUUID(), venueId, eventTitle, startZonedDateTime, endZonedDateTime, EventStatus.UPCOMING, null));

        System.out.println("\nEvent added successfully.");
    }

    public void listEvents() {
        System.out.println("\nList of Events:");
        for (Event event : ticketingRepository.findAllEvents()) {
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
}
