import java.util.Scanner;

/**
 * =========================================================
 * MAIN CLASS - UseCase9ErrorHandlingValidation
 * =========================================================
 *
 * Use Case 9: Error Handling & Validation
 *
 * Description:
 * This class demonstrates how user input
 * is validated before booking is processed.
 *
 * The system:
 * - Accepts user input
 * - Validates input centrally
 * - Handles errors gracefully
 *
 * @version 9.0
 */
public class UseCase9ErrorHandlingValidation {

    /**
     * Application entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        // Display application header
        System.out.println("Booking Validation");

        Scanner scanner = new Scanner(System.in);

        // Initialize required components
        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        try {

            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            // Validate before processing
            validator.validate(guestName, roomType, inventory);

            // If valid, add to booking queue
            Reservation reservation = new Reservation(guestName, roomType);
            bookingQueue.addRequest(reservation);
            System.out.println("Booking request added for " + guestName + " (" + roomType + ").");

        } catch (InvalidBookingException e) {

            // Handle domain-specific validation errors
            System.out.println("Booking failed: " + e.getMessage());

        } finally {
            scanner.close();
        }
    }
}
