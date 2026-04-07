import java.util.Stack;

/**
 * =========================================================
 * CLASS - CancellationService
 * =========================================================
 *
 * Use Case 10: Booking Cancellation & Inventory Rollback
 *
 * Description:
 * This class is responsible for cancelling
 * confirmed bookings and restoring system state.
 *
 * A Stack is used to track released room IDs,
 * supporting LIFO rollback behavior.
 *
 * @version 10.0
 */
public class CancellationService {

    /**
     * Stack that records released room IDs.
     *
     * LIFO order models rollback behavior:
     * the most recent release is the first to be tracked.
     */
    private Stack<String> rollbackStack;

    /**
     * Initializes the cancellation service
     * with an empty rollback stack.
     */
    public CancellationService() {
        rollbackStack = new Stack<>();
    }

    /**
     * Cancels a confirmed booking and
     * restores inventory for the room type.
     *
     * @param guestName   name of the guest requesting cancellation
     * @param roomId      allocated room ID to release
     * @param roomType    type of the room being cancelled
     * @param inventory   centralized room inventory
     * @param history     booking history to update
     * @throws InvalidBookingException if the reservation cannot be cancelled
     */
    public void cancel(
            String guestName,
            String roomId,
            String roomType,
            RoomInventory inventory,
            BookingHistory history
    ) throws InvalidBookingException {

        // Validate that reservation exists in history
        boolean found = history.getConfirmedReservations().stream()
                .anyMatch(r -> r.getGuestName().equals(guestName)
                            && r.getRoomType().equals(roomType));

        if (!found) {
            throw new InvalidBookingException(
                "No confirmed booking found for guest: " + guestName);
        }

        // Record released room ID in rollback stack
        rollbackStack.push(roomId);

        // Restore inventory count
        int current = inventory.getRoomAvailability()
                .getOrDefault(roomType, 0);
        inventory.updateAvailability(roomType, current + 1);

        // Remove from booking history
        history.getConfirmedReservations().removeIf(
            r -> r.getGuestName().equals(guestName)
              && r.getRoomType().equals(roomType)
        );

        System.out.println("Booking cancelled for Guest: " + guestName
                + ", Room ID: " + roomId + " has been released.");
        System.out.println("Rolled back room: " + rollbackStack.peek());
    }

    /**
     * Returns the rollback stack for inspection.
     *
     * @return stack of released room IDs
     */
    public Stack<String> getRollbackStack() { return rollbackStack; }
}
