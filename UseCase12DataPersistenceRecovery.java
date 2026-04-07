/**
 * ========================================================
 * MAIN CLASS - UseCase12DataPersistenceRecovery
 * ========================================================
 *
 * Use Case 12: Data Persistence & System Recovery
 *
 * Description:
 * This class demonstrates how system state
 * can be restored after an application restart.
 *
 * Inventory data is loaded from a file
 * before any booking operations occur.
 *
 * @version 12.0
 */
public class UseCase12DataPersistenceRecovery {

    /**
     * Application entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        String filePath = "inventory_state.txt";

        FilePersistenceService persistenceService = new FilePersistenceService();
        RoomInventory inventory = new RoomInventory();

        // ── System Recovery ──────────────────────────────────────
        System.out.println("System Recovery");
        persistenceService.loadInventory(inventory, filePath);

        // If no prior save exists, initialize default inventory
        if (!inventory.hasData()) {
            inventory.setAvailableCount("Single", 5);
            inventory.setAvailableCount("Double", 3);
            inventory.setAvailableCount("Suite",  2);
        }

        // ── Display Current Inventory ─────────────────────────────
        System.out.println("\nCurrent Inventory:");
        System.out.println("Single: " + inventory.getAvailableCount("Single"));
        System.out.println("Double: " + inventory.getAvailableCount("Double"));
        System.out.println("Suite:  " + inventory.getAvailableCount("Suite"));

        // ── Persist State ─────────────────────────────────────────
        persistenceService.saveInventory(inventory, filePath);
    }
}
