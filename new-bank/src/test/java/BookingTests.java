import ch.bbw.Booking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests für die Klasse Booking.
 *
 * @author Luigi Cavuoti, Ledion Sakaj
 * @version 2.0
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

	/**
	 * Ein leerer oder fehlender Text wird durch den Standardtext ersetzt.
	 */
	@Test
	@DisplayName("Null or blank text becomes the default text \"Buchung\"")
	public void testDefaultText() {
		assertEquals("Buchung", new Booking(1_700_000_000_000L, 500_000, null).text());
		assertEquals("Buchung", new Booking(1_700_000_000_000L, 500_000, "").text());
		assertEquals("Buchung", new Booking(1_700_000_000_000L, 500_000, "   ").text());
	}

	/**
	 * Eine Auszahlung wird als Buchung mit negativem Betrag gespeichert.
	 */
	@Test
	@DisplayName("A booking can hold a negative amount (withdrawal)")
	public void testNegativeAmount() {
		Booking booking = new Booking(1_700_000_000_000L, -120_000, "Miete");

		assertEquals(-120_000, booking.amount());
	}

	/**
	 * Zwei Buchungen mit denselben Werten sind gleich (record-Gleichheit).
	 */
	@Test
	@DisplayName("Bookings with the same values are equal")
	public void testEquality() {
		Booking a = new Booking(1_700_000_000_000L, 500_000, "Lohn");
		Booking b = new Booking(1_700_000_000_000L, 500_000, "Lohn");
		Booking c = new Booking(1_700_000_000_000L, 500_000, "Miete");

		assertEquals(a, b);
		assertNotEquals(a, c);
	}
}
