/**
 * ========================================================
 * MAIN CLASS - UseCase8BookingHistoryReport
 * ========================================================
 *
 * Use Case 8: Booking History & Reporting
 *
 * Description:
 * This class demonstrates how
 * confirmed bookings are stored
 * and reported.
 *
 * The system maintains an ordered
 * audit trail of reservations.
 *
 * @version 8.0
 */
public class UseCase8BookingHistoryReport {

    /**
     * Application entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // Create booking history
        BookingHistory history = new BookingHistory();

        // Add confirmed reservations
        history.addReservation(new Reservation("Single-1", "Abhi", "Single"));
        history.addReservation(new Reservation("Double-1", "Subha", "Double"));
        history.addReservation(new Reservation("Suite-1", "Vanmathi", "Suite"));

        // Generate report
        BookingReportService reportService = new BookingReportService();
        System.out.println("Booking History and Reporting");
        System.out.println();
        reportService.generateReport(history);
    }
}
