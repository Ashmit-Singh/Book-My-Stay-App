import java.util.Scanner;

/**
 * =========================================================
 * MAIN CLASS - UseCase10BookingCancellation
 * =========================================================
 *
 * Use Case 10: Booking Cancellation & Inventory Rollback
 *
 * Description:
 * This class demonstrates safe cancellation
 * of confirmed bookings with inventory rollback.
 *
 * The system:
 * - Accepts a booking, confirms it
 * - Accepts a cancellation request
 * - Validates and rolls back state
 *
 * @version 10.0
 */
public class UseCase10BookingCancellation {

    /**
     * Application entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        System.out.println("Booking Cancellation");

        Scanner scanner = new Scanner(System.in);

        // Initialize required components
        RoomInventory inventory        = new RoomInventory();
        RoomAllocationService allocSvc = new RoomAllocationService();
        BookingHistory history         = new BookingHistory();
        CancellationService cancelSvc  = new CancellationService();

        try {

            // Step 1: Make a booking
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            Reservation reservation = new Reservation(guestName, roomType);
            allocSvc.allocateRoom(reservation, inventory);
            history.addReservation(reservation);

            // Step 2: Cancel the booking
            System.out.print("Enter room ID to cancel (e.g. Single-1): ");
            String roomId = scanner.nextLine();

            cancelSvc.cancel(guestName, roomId, roomType, inventory, history);

            // Step 3: Show restored inventory
            System.out.println("Updated inventory after cancellation:");
            inventory.getRoomAvailability()
                     .forEach((type, count) ->
                         System.out.println("  " + type + ": " + count + " available"));

        } catch (InvalidBookingException e) {
            System.out.println("Cancellation failed: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
