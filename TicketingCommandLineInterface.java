import java.util.Scanner;

import repositories.TicketingRepository;
import services.EventService;
import services.ReservationService;
import services.SeatService;
import services.VenueService;

public class TicketingCommandLineInterface {
    public static void main(String[] args) {

    Scanner scan = new Scanner(System.in);

    TicketingRepository ticketingRepository = new TicketingRepository();
    SeatService seatService = new SeatService(scan, ticketingRepository);
    VenueService venueService = new VenueService(scan, seatService, ticketingRepository);
    EventService eventService = new EventService(scan, ticketingRepository);
    ReservationService reservationService = new ReservationService(scan, ticketingRepository);

    ticketingRepository.load();

    boolean restartTicketingSystem = true;

    do {
        System.out.println("\n\n\n// = = = = = = = = = = Welcome to Atlas Ticketing System = = = = = = = = = = //\n");
        System.out.print("\nAre you Operator or Customer? (Enter 'O' for Operator, 'C' for Customer): ");

        char userType = scan.next().charAt(0);

        if (userType == 'O' || userType == 'o') {
            System.out.println("\nYou are logged in as an Operator. What would you like to do?");
            System.out.println("\n1. Add Venue\t2. Add Event\t3. Add Seats\t4. List Venues\t5. List Events\t6. List Seats\t7. Exit");
            System.out.print("\nPlease select an option (1, 2, 3, 4, 5, 6 or 7): ");

            if (!scan.hasNextInt()) {
                System.out.println("\nPlease enter a valid integer.");
                scan.nextLine();
                continue;
            }
            
            int operatorChoice = scan.nextInt();
            scan.nextLine();

            switch (operatorChoice) {
                case 1:
                    venueService.addVenue();
                    ticketingRepository.save();
                    break;
                case 2:
                    venueService.listVenues();
                    eventService.addEvent();
                    ticketingRepository.save();
                    break;
                case 3:
                    venueService.listVenues();
                    seatService.addSeats();
                    ticketingRepository.save();
                    break;
                case 4:
                    venueService.listVenues();
                    break;

                case 5:
                    eventService.listEvents();
                    break;
                case 6:
                    seatService.listSeats();
                    break;
                case 7:
                    restartTicketingSystem = false;
                    System.out.println("\nExiting the Ticketing System. Goodbye!");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select a valid option.");
            }
        } else if (userType == 'C' || userType == 'c') {
            System.out.println("\nYou are logged in as a Customer. What would you like to do?");
            System.out.println("\n1. List Venues\t2. List Events\t3. Make Reservation\t4. Confirm Reservation\t5. Cancel Reservation\t6. Exit");
            System.out.print("\nPlease select an option (1, 2, 3, 4, 5 or 6): ");

            if (!scan.hasNextInt()) {
                System.out.println("\nPlease enter a valid integer.");
                scan.nextLine();
                continue;
            }

            int customerChoice = scan.nextInt();
            scan.nextLine();

            switch (customerChoice) {
                case 1:
                    venueService.listVenues();
                    break;
                case 2:
                    eventService.listEvents();
                    break;
                case 3:
                    eventService.listEvents();
                    reservationService.makeReservation();
                    ticketingRepository.save();
                    break;
                case 4:
                    reservationService.confirmReservation();
                    ticketingRepository.save();
                    break;
                case 5:
                    reservationService.cancelReservation();
                    ticketingRepository.save();
                    break;
                case 6:
                    restartTicketingSystem = false;
                    System.out.println("\nExiting the Ticketing System. Goodbye!");
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
}
