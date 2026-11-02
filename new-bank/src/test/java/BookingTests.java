import ch.bbw.Booking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests für die Klasse Booking.
 *
 * @author Luigi Cavuoti
 * @version 1.1
 */
public class BookingTests {
	/**
	 * Tests f�r die Erzeugung von Buchungen.
	 */
	@Test
	@DisplayName("Construct Simple Booking, should work ")
	public void testInitialization() {
		Booking booking = new Booking(1_700_000_000_000L, 500_000, "Lohn");

		// eine Buchung gibt genau die Werte zurück, mit denen sie erstellt wurde
		assertEquals(1_700_000_000_000L, booking.date());
		assertEquals(500_000, booking.amount());
		assertEquals("Lohn", booking.text());
	}

	/**
	 * Experimente mit print().
	 */
	@Test
	@DisplayName("Not a real test, it prints a list of bookings")
	public void testPrint() {
		List<Booking> bookings = List.of(
				new Booking(1_700_000_000_000L, 500_000, "Lohn"),
				new Booking(1_700_000_100_000L, -120_000, "Miete"));

		for (Booking booking : bookings) {
			System.out.println(booking);
			// ein record gibt beim Ausgeben alle Werte aus, also auch den Text
			assertTrue(booking.toString().contains(booking.text()));
		}
	}
}
