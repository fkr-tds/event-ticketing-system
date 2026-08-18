package services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
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

        Venue venueToAddEvent = ticketingRepository.findVenueById(venueId);

        if (venueToAddEvent == null) {
            System.out.println("\nVenue with ID " + venueId + " does not exist.");
            return;
        }

        List<Seat> seatsInTheVenue = ticketingRepository.findSeatsByVenueId(venueId);

        if (seatsInTheVenue.isEmpty()) {
            System.out.println("\nVenue with ID " + venueId + " does not have any seats.");
            return;
        }

        System.out.print("\nEnter the event title: ");
        String eventTitle = scan.nextLine().trim();

        if (eventTitle.isBlank()) {
            System.out.println("\nEvent title cannot be empty.");
            return;
        }

        System.out.print("\nDate [yyyy-MM-dd] (eg. 2027-01-01): ");
        String dateString = scan.nextLine().trim();

        LocalDate date;

        try {
            date = LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date. Please enter the date in the format yyyy-MM-dd.");
            return;
        }

        System.out.print("\nTime [HH:mm] (eg. 01:01): ");
        String timeString = scan.nextLine().trim();

        LocalTime time;

        try {
            time = LocalTime.parse(timeString);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time. Please enter the time in the format HH:mm.");
            return;
        }

        LocalDateTime startLocalDateTime = LocalDateTime.of(date, time);

        ZoneId venueTimezone = venueToAddEvent.timezone();

        ZonedDateTime startZonedDateTime = startLocalDateTime.atZone(venueTimezone);

        if (startZonedDateTime.isBefore(ZonedDateTime.now(venueTimezone))) {
            System.out.println("\nPlease enter a future date or time.");
            return;
        }

        System.out.print("\nEnter the event duration in minutes: ");

        if (!scan.hasNextInt()) {
            System.out.println("\nPlease enter a valid integer.");
            scan.nextLine();
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

        for (Event existingEvent : ticketingRepository.findAllEvents()) {

            if (!existingEvent.venueId().equals(venueId)) {
                continue;
            }

            if (startZonedDateTime.isBefore(existingEvent.end()) && endZonedDateTime.isAfter(existingEvent.start())) {
                System.out.println("\nThe venue is already occupied by event '" + existingEvent.title() + "' during the selected time.");
                return;
            }
        }
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
