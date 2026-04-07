/**
 * =========================================================
 * CLASS - ConcurrentBookingProcessor
 * =========================================================
 *
 * Use Case 11: Concurrent Booking Simulation
 *
 * Description:
 * This class represents a booking processor
 * that can be executed by multiple threads.
 *
 * It demonstrates how shared resources
 * such as booking queues and inventory
 * must be accessed in a thread-safe manner.
 *
 * @version 11.0
 */
public class ConcurrentBookingProcessor implements Runnable {

    /**
     * Shared booking request queue.
     */
    private BookingRequestQueue bookingQueue;

    /**
     * Shared room inventory.
     */
    private RoomInventory inventory;

    /**
     * Shared room allocation service.
     */
    private RoomAllocationService allocationService;

    /**
     * Creates a new booking processor.
     *
     * @param bookingQueue     shared booking queue
     * @param inventory        shared inventory
     * @param allocationService shared allocation service
     */
    public ConcurrentBookingProcessor(
            BookingRequestQueue bookingQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService
    ) {
        this.bookingQueue      = bookingQueue;
        this.inventory         = inventory;
        this.allocationService = allocationService;
    }

    /**
     * Executes booking processing logic.
     *
     * This method is called when the thread starts.
     */
    @Override
    public void run() {

        while (true) {

            Reservation reservation;

            /*
             * Synchronize on the booking queue to ensure
             * that only one thread can retrieve a request
             * at a time.
             */
            synchronized (bookingQueue) {
                if (!bookingQueue.hasPendingRequests()) {
                    break;
                }
                reservation = bookingQueue.getNextRequest();
            }

            /*
             * Allocation also mutates shared inventory.
             * Synchronization ensures atomic allocation.
             */
            synchronized (inventory) {
                allocationService.allocateRoom(reservation, inventory);
            }
        }
    }
}
