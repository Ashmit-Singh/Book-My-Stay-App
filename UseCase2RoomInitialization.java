/**
 * ===========================================================
 * MAIN CLASS - UseCase2RoomInitialization
 * ===========================================================
 *
 * Use Case 2: Basic Room Types & Static Availability
 *
 * Description:
 * This class demonstrates room initialization
 * using domain models before introducing
 * centralized inventory management.
 *
 * Availability is represented using
 * simple variables to highlight limitations.
 *
 * @version 2.1
 */
public class UseCase2RoomInitialization {

    /**
     * Application entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        // Create one instance of each room type
        SingleRoom singleRoom = new SingleRoom();
        DoubleRoom doubleRoom = new DoubleRoom();
        SuiteRoom  suiteRoom  = new SuiteRoom();

        // Static availability — represented as simple integer variables
        // Limitation: adding more rooms means adding more variables manually
        int singleRoomAvailable = 5;
        int doubleRoomAvailable = 3;
        int suiteRoomAvailable  = 2;

        // Display inventory
        System.out.println("Hotel Room Initialization");
        System.out.println();

        System.out.println("Single Room:");
        singleRoom.displayRoomDetails();
        System.out.println("Available: " + singleRoomAvailable);
        System.out.println();

        System.out.println("Double Room:");
        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + doubleRoomAvailable);
        System.out.println();

        System.out.println("Suite Room:");
        suiteRoom.displayRoomDetails();
        System.out.println("Available: " + suiteRoomAvailable);
    }
}
