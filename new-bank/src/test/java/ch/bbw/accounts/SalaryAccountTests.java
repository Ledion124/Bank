package ch.bbw.accounts;

import ch.bbw.Booking;
import ch.bbw.Scheduled;
import ch.bbw.exceptions.InvalidAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tests der Klasse SalaryAccount.
 *
 * <p>
 * Beträge in Millirappen: 100_000 = CHF 1.00.
 * </p>
 *
 * @author Luigi Cavuoti
 * @version 1.2
 */
public class SalaryAccountTests
{
	/**
	 * Kreditlimite von CHF -500.00.
	 */
	private static final long CREDIT_LIMIT = -50_000_000;

	/**
	 * Erstellt ein Lohnkonto und testet eine einfache Einzahlung.
	 */
	@Test
	@DisplayName("Constructs a SalaryAccount and tests a simple deposit")
	public void test() throws Exception
	{
		SalaryAccount account = new SalaryAccount("P-1000", CREDIT_LIMIT);

		long balance = account.deposit(Scheduled.now(), 10_000_000);

		assertEquals("P-1000", account.getId());
		assertEquals(10_000_000, balance);
		assertEquals(10_000_000, account.getBalance());
	}

	/**
	 * Der Getter gibt die Kreditlimite aus dem Konstruktor zurück.
	 */
	@Test
	@DisplayName("getCreditLimit returns the limit passed to the constructor")
	public void testGetCreditLimit()
	{
		SalaryAccount account = new SalaryAccount("P-1000", CREDIT_LIMIT);

		assertEquals(CREDIT_LIMIT, account.getCreditLimit());
	}

	/**
	 * Abheben bis genau zur Kreditlimite funktioniert, der Saldo ist danach negativ.
	 */
	@Test
	@DisplayName("Withdraw down to exactly the creditLimit should work")
	public void testWithdrawToCreditLimit() throws Exception
	{
		SalaryAccount account = new SalaryAccount("P-1000", CREDIT_LIMIT);

		long balance = account.withdraw(Scheduled.now(), 50_000_000);

		assertEquals(CREDIT_LIMIT, balance);
		assertEquals(CREDIT_LIMIT, account.getBalance());
		assertTrue(account.getBalance() < 0);
	}

	/**
	 * Ein Millirappen über die Kreditlimite hinaus wird abgelehnt.
	 */
	@Test
	@DisplayName("Withdraw one Millirappen beyond the creditLimit should throw")
	public void testWithdrawBeyondCreditLimit()
	{
		SalaryAccount account = new SalaryAccount("P-1000", CREDIT_LIMIT);

		assertThrows(InvalidAmountException.class,
				() -> account.withdraw(Scheduled.now(), 50_000_001));
	}

	/**
	 * Mit Kreditlimite 0 kann das Konto nicht überzogen werden.
	 */
	@Test
	@DisplayName("creditLimit 0 means no overdraft")
	public void testZeroCreditLimit() throws Exception
	{
		SalaryAccount account = new SalaryAccount("P-1000", 0);

		assertThrows(InvalidAmountException.class,
				() -> account.withdraw(Scheduled.now(), 1));

		account.deposit(Scheduled.now(), 100_000);
		assertEquals(0, account.withdraw(Scheduled.now(), 100_000));
	}

	/**
	 * Eine abgelehnte Abhebung verändert weder Saldo noch Buchungen.
	 *
	 * <p>
	 * Benötigt {@code getBookings()} aus Sprint 2.
	 * </p>
	 */
	@Test
	@DisplayName("Rejected withdraw leaves balance and bookings unchanged")
	public void testRejectedWithdrawLeavesAccountUnchanged() throws Exception
	{
		SalaryAccount account = new SalaryAccount("P-1000", CREDIT_LIMIT);
		account.deposit(Scheduled.now(), 10_000_000);
		List<Booking> bookingsBefore = List.copyOf(account.getBookings());

		assertThrows(InvalidAmountException.class,
				() -> account.withdraw(Scheduled.now(), 60_000_001));

		assertEquals(10_000_000, account.getBalance());
		assertEquals(1, account.getBookings().size());
		assertEquals(bookingsBefore, account.getBookings());
	}

	/**
	 * Abheben mit Buchungstext bis genau zur Kreditlimite funktioniert,
	 * der Text landet in der Buchung.
	 */
	@Test
	@DisplayName("Withdraw with text down to exactly the creditLimit should work")
	public void testWithdrawWithTextToCreditLimit() throws Exception
	{
		SalaryAccount account = new SalaryAccount("P-1000", CREDIT_LIMIT);

		long balance = account.withdraw(Scheduled.now(), 50_000_000, "Miete");

		assertEquals(CREDIT_LIMIT, balance);
		assertEquals(1, account.getBookings().size());
		assertEquals("Miete", account.getBookings().get(0).text());
	}

	/**
	 * Auch beim Abheben mit Buchungstext gilt die Kreditlimite.
	 */
	@Test
	@DisplayName("Withdraw with text beyond the creditLimit should throw")
	public void testWithdrawWithTextBeyondCreditLimit()
	{
		SalaryAccount account = new SalaryAccount("P-1000", CREDIT_LIMIT);

		assertThrows(InvalidAmountException.class,
				() -> account.withdraw(Scheduled.now(), 50_000_001, "Miete"));

		assertEquals(0, account.getBalance());
		assertTrue(account.getBookings().isEmpty());
	}

	/**
	 * Eine positive Kreditlimite ist unsinnig und wird abgelehnt.
	 */
	@Test
	@DisplayName("Positive creditLimit should throw IllegalArgumentException")
	public void testPositiveCreditLimit()
	{
		assertThrows(IllegalArgumentException.class,
				() -> new SalaryAccount("P-1000", 100_000));
	}
}
