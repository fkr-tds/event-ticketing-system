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
    static Scanner scan = new Scanner(System.in);

    public static void addEvent() {
        VenueService.listVenues();
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

        System.out.println("\nEvent added successfully.");
    }

    public static void listEvents() {
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
}
