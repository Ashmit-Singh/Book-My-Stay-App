/**
 * =========================================================
 * MAIN CLASS - UseCase11ConcurrentBookingSimulation
 * =========================================================
 *
 * Use Case 11: Concurrent Booking Simulation
 *
 * Description:
 * This class simulates multiple users
 * attempting to book rooms at the same time.
 *
 * It highlights race conditions and
 * demonstrates how synchronization
 * prevents inconsistent allocations.
 *
 * @version 11.0
 */
public class UseCase11ConcurrentBookingSimulation {

    /**
     * Application entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        System.out.println("Concurrent Booking Simulation");

        // Initialize shared resources
        RoomInventory inventory               = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();
        BookingRequestQueue bookingQueue      = new BookingRequestQueue();

        // Add concurrent booking requests
        bookingQueue.addRequest(new Reservation("Abhi",     "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Double"));
        bookingQueue.addRequest(new Reservation("Kural",    "Suite"));
        bookingQueue.addRequest(new Reservation("Subha",    "Single"));

        // Create booking processor tasks
        Thread t1 = new Thread(
                new ConcurrentBookingProcessor(
                        bookingQueue, inventory, allocationService
                )
        );

        Thread t2 = new Thread(
                new ConcurrentBookingProcessor(
                        bookingQueue, inventory, allocationService
                )
        );

        // Start concurrent processing
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        // Display remaining inventory
        System.out.println("\nRemaining Inventory:");
        inventory.getRoomAvailability()
                 .forEach((type, count) ->
                     System.out.println(type + ": " + count));
    }
}
