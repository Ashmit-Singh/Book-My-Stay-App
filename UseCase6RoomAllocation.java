/**
 * ===========================================================
 * MAIN CLASS - UseCase6RoomAllocation
 * ===========================================================
 *
 * Use Case 6: Reservation Confirmation & Room Allocation
 *
 * Description:
 * This class demonstrates how booking
 * requests are confirmed and rooms
 * are allocated safely.
 *
 * It consumes booking requests in FIFO
 * order and updates inventory immediately.
 *
 * @version 6.0
 */
public class UseCase6RoomAllocation {

    /**
     * Application entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        // Display application header
        System.out.println("Room Allocation Processing");

        // Initialize centralized inventory
        RoomInventory inventory = new RoomInventory();

        // Initialize booking queue (from UC5)
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Initialize allocation service
        RoomAllocationService allocationService = new RoomAllocationService();

        // Create and queue booking requests
        bookingQueue.addRequest(new Reservation("Abhi",     "Single"));
        bookingQueue.addRequest(new Reservation("Subha",    "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite"));

        // Process each request in FIFO order — confirm and allocate
        while (bookingQueue.hasPendingRequests()) {
            Reservation next = bookingQueue.getNextRequest();
            allocationService.allocateRoom(next, inventory);
        }
    }
}
