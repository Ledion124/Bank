package ch.bbw.accounts;

import ch.bbw.Booking;
import ch.bbw.Scheduled;
import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Tests für die Klasse PromoYouthSavingsAccount.
 *
 * @author Silvan Castelberg
 * @version 1.0
 */
public class PromoYouthSavingsAccountTests
{
	/**
	 * 100 CHF in Millirappen.
	 */
	private static final long HUNDRED_CHF = 10_000_000L;

	private PromoYouthSavingsAccount account;

	/**
	 * Erstellt vor jedem Test ein Konto mit CHF 100.00 Saldo.
	 */
	@BeforeEach
	public void setUp() throws InvalidAmountException, InvalidDateException
	{
		account = new PromoYouthSavingsAccount("Y-1000");
		account.deposit(Scheduled.now(), HUNDRED_CHF);
	}

	/**
	 * Der Konstruktor setzt die Kontonummer, die Einzahlung erhöht den Saldo.
	 */
	@Test
	@DisplayName("Constructs a PromoYouthSavingsAccount and tests a simple deposit")
	public void testInitAndDeposit()
	{
		assertEquals("Y-1000", account.getId());
		assertEquals(HUNDRED_CHF, account.getBalance());
	}

	/**
	 * Abheben bis genau zum Mindestsaldo funktioniert.
	 */
	@Test
	@DisplayName("Withdrawing down to exactly the minimum balance should work")
	public void testWithdrawDownToMinBalance() throws InvalidAmountException, InvalidDateException
	{
		long amount = HUNDRED_CHF - PromoYouthSavingsAccount.MIN_BALANCE;

		long newBalance = account.withdraw(Scheduled.now(), amount);

		assertEquals(PromoYouthSavingsAccount.MIN_BALANCE, newBalance);
		assertEquals(PromoYouthSavingsAccount.MIN_BALANCE, account.getBalance());
	}

	/**
	 * Ein Millirappen unter den Mindestsaldo wird abgelehnt.
	 */
	@Test
	@DisplayName("Withdrawing one Millirappen below the minimum balance should throw")
	public void testWithdrawBelowMinBalance()
	{
		long amount = HUNDRED_CHF - PromoYouthSavingsAccount.MIN_BALANCE + 1;

		assertThrows(InvalidAmountException.class, () -> account.withdraw(Scheduled.now(), amount));
	}

	/**
	 * Eine abgelehnte Abhebung verändert den Saldo nicht.
	 */
	@Test
	@DisplayName("A rejected withdrawal should leave the balance untouched")
	public void testRejectedWithdrawLeavesBalance()
	{
		assertThrows(InvalidAmountException.class, () -> account.withdraw(Scheduled.now(), HUNDRED_CHF));

		assertEquals(HUNDRED_CHF, account.getBalance());
	}

	/**
	 * Von einem Konto, das genau den Mindestsaldo hat, kann nichts abgehoben werden.
	 */
	@Test
	@DisplayName("Withdrawing from an account below the minimum balance should throw")
	public void testWithdrawFromSmallAccount() throws InvalidAmountException, InvalidDateException
	{
		var small = new PromoYouthSavingsAccount("Y-1001");
		small.deposit(Scheduled.now(), PromoYouthSavingsAccount.MIN_BALANCE);

		assertThrows(InvalidAmountException.class, () -> small.withdraw(Scheduled.now(), 1));
		assertEquals(PromoYouthSavingsAccount.MIN_BALANCE, small.getBalance());
	}

	/**
	 * Die geerbte Prüfung lehnt einen Betrag von 0 oder kleiner ab.
	 */
	@Test
	@DisplayName("Inherited validation: a non-positive amount should still throw")
	public void testWithdrawInvalidAmount()
	{
		assertThrows(InvalidAmountException.class, () -> account.withdraw(Scheduled.now(), 0));
		assertThrows(InvalidAmountException.class, () -> account.withdraw(Scheduled.now(), -1));
	}

	/**
	 * Das Konto kann als SavingsAccount und als Account verwendet werden.
	 */
	@Test
	@DisplayName("A PromoYouthSavingsAccount is usable as a SavingsAccount and an Account")
	public void testIsSavingsAccount()
	{
		SavingsAccount savings = account;
		Account plain = account;

		assertEquals(HUNDRED_CHF, savings.getBalance());
		assertEquals(HUNDRED_CHF, plain.getBalance());
	}

	/**
	 * Abheben mit Buchungstext bis genau zum Mindestsaldo funktioniert,
	 * der Text landet in der Buchung.
	 */
	@Test
	@DisplayName("Withdraw with text down to exactly the minimum balance should work")
	public void testWithdrawWithTextDownToMinBalance() throws InvalidAmountException, InvalidDateException
	{
		long amount = HUNDRED_CHF - PromoYouthSavingsAccount.MIN_BALANCE;

		long newBalance = account.withdraw(Scheduled.now(), amount, "Kino");

		assertEquals(PromoYouthSavingsAccount.MIN_BALANCE, newBalance);
		assertEquals(2, account.getBookings().size());
		assertEquals("Kino", account.getBookings().get(1).text());
	}

	/**
	 * Auch beim Abheben mit Buchungstext gilt der Mindestsaldo:
	 * Saldo und Buchungen bleiben unverändert.
	 */
	@Test
	@DisplayName("Withdraw with text below the minimum balance should throw and leave the account unchanged")
	public void testWithdrawWithTextBelowMinBalance()
	{
		long amount = HUNDRED_CHF - PromoYouthSavingsAccount.MIN_BALANCE + 1;
		List<Booking> bookingsBefore = account.getBookings();

		assertThrows(InvalidAmountException.class, () -> account.withdraw(Scheduled.now(), amount, "Kino"));

		assertEquals(HUNDRED_CHF, account.getBalance());
		assertEquals(bookingsBefore, account.getBookings());
	}

	/**
	 * Je ein Konto pro Typ in einer {@code List<Account>}: Die Summe der Salden
	 * stimmt, ein überzogenes Lohnkonto senkt die Summe.
	 */
	@Test
	@DisplayName("Sum of balances over a List<Account> with one account per type should be correct")
	public void testPolymorphicSumOfBalances() throws InvalidAmountException, InvalidDateException
	{
		Account savings = new SavingsAccount("S-1000");
		savings.deposit(Scheduled.now(), HUNDRED_CHF);

		Account salary = new SalaryAccount("P-1000", -50_000_000);
		salary.withdraw(Scheduled.now(), 5_000_000);

		List<Account> accounts = List.of(savings, account, salary);

		long sum = 0;
		for (Account a : accounts)
			sum += a.getBalance();

		assertEquals(-5_000_000, salary.getBalance());
		assertEquals(HUNDRED_CHF + HUNDRED_CHF - 5_000_000, sum);
	}
}
