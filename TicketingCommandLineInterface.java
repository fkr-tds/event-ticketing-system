import java.util.Scanner;

import repositories.TicketingRepository;
import services.EventService;
import services.SeatService;
import services.VenueService;

public class TicketingCommandLineInterface {
    public static void main(String[] args) {

    Scanner scan = new Scanner(System.in);

    TicketingRepository ticketingRepository = new TicketingRepository();
    SeatService seatService = new SeatService(scan, ticketingRepository);
    VenueService venueService = new VenueService(scan, seatService, ticketingRepository);
    EventService eventService = new EventService(scan, ticketingRepository);

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
                    venueService.addVenue();
                    break;
                case 2:
                    venueService.listVenues();
                    break;
                case 3:
                    venueService.listVenues();
                    eventService.addEvent();
                    break;
                case 4:
                    eventService.listEvents();
                    break;
                case 5:
                    venueService.listVenues();
                    seatService.addSeats();
                    break;
                case 6:
                    venueService.listVenues();
                    seatService.listSeats();
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
                    venueService.listVenues();
                    break;
                case 2:
                    eventService.listEvents();
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
