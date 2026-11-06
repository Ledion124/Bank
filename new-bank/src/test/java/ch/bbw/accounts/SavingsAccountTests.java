package ch.bbw.accounts;

import ch.bbw.Scheduled;
import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Tests für die Klasse SavingsAccount.
 *
 * <p>
 * Beträge in Millirappen: 100_000 = CHF 1.00.
 * </p>
 *
 * @author Luigi Cavuoti
 * @version 1.1
 */
public class SavingsAccountTests
{
	/**
	 * Erstellt ein Sparkonto und testet eine einfache Einzahlung.
	 */
	@Test
	@DisplayName("Constructs a SavingsAccount and tests a simple deposit")
	public void test() throws Exception
	{
		SavingsAccount account = new SavingsAccount("S-1000");

		long balance = account.deposit(Scheduled.now(), 10_000_000);

		assertEquals(10_000_000, balance);
		assertEquals(10_000_000, account.getBalance());
	}

	/**
	 * Genau den Saldo abheben funktioniert.
	 */
	@Test
	@DisplayName("Withdraw exactly the balance should work")
	public void testWithdrawExactBalance() throws Exception
	{
		SavingsAccount account = new SavingsAccount("S-1000");
		account.deposit(Scheduled.now(), 10_000_000);

		long balance = account.withdraw(Scheduled.now(), 10_000_000);

		assertEquals(0, balance);
		assertEquals(0, account.getBalance());
	}

	/**
	 * Ein Millirappen mehr als der Saldo wird abgelehnt, der Saldo bleibt gleich.
	 */
	@Test
	@DisplayName("Withdraw one Millirappen more than the balance should throw")
	public void testWithdrawBeyondBalance() throws Exception
	{
		SavingsAccount account = new SavingsAccount("S-1000");
		account.deposit(Scheduled.now(), 10_000_000);

		assertThrows(InvalidAmountException.class,
				() -> account.withdraw(Scheduled.now(), 10_000_001));

		assertEquals(10_000_000, account.getBalance());
	}

	/**
	 * Von einem leeren Sparkonto kann nichts abgehoben werden.
	 */
	@Test
	@DisplayName("Withdraw from an empty SavingsAccount should throw")
	public void testWithdrawFromEmptyAccount()
	{
		SavingsAccount account = new SavingsAccount("S-1000");

		assertThrows(InvalidAmountException.class,
				() -> account.withdraw(Scheduled.now(), 1));

		assertEquals(0, account.getBalance());
	}

	/**
	 * Ein Betrag von 0 oder kleiner wird abgelehnt.
	 */
	@Test
	@DisplayName("Withdraw of a non-positive amount should throw")
	public void testWithdrawInvalidAmount() throws Exception
	{
		SavingsAccount account = new SavingsAccount("S-1000");
		account.deposit(Scheduled.now(), 10_000_000);

		assertThrows(InvalidAmountException.class,
				() -> account.withdraw(Scheduled.now(), 0));
		assertThrows(InvalidAmountException.class,
				() -> account.withdraw(Scheduled.now(), -1));

		assertEquals(10_000_000, account.getBalance());
	}

	/**
	 * Eine Abhebung mit einem Datum in der Vergangenheit wird abgelehnt.
	 */
	@Test
	@DisplayName("Withdraw with a past date should throw")
	public void testWithdrawPastDate() throws Exception
	{
		SavingsAccount account = new SavingsAccount("S-1000");
		account.deposit(Scheduled.now(), 10_000_000);

		assertThrows(InvalidDateException.class,
				() -> account.withdraw(Scheduled.now().minusDays(1), 100_000));

		assertEquals(10_000_000, account.getBalance());
	}

	/**
	 * Ein Sparkonto gibt als Kontotyp "Sparkonto" zurück.
	 */
	@Test
	@DisplayName("getAccountType of a SavingsAccount should be Sparkonto")
	public void testGetAccountType()
	{
		Account account = new SavingsAccount("S-1000");

		assertEquals("Sparkonto", account.getAccountType());
	}
}
