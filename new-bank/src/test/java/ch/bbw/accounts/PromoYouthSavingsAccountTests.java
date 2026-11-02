package ch.bbw.accounts;

import ch.bbw.Scheduled;
import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

	@BeforeEach
	public void setUp() throws InvalidAmountException, InvalidDateException
	{
		account = new PromoYouthSavingsAccount("Y-1000");
		account.deposit(Scheduled.now(), HUNDRED_CHF);
	}

	@Test
	@DisplayName("Constructs a PromoYouthSavingsAccount and tests a simple deposit")
	public void testInitAndDeposit()
	{
		assertEquals("Y-1000", account.getId());
		assertEquals(HUNDRED_CHF, account.getBalance());
	}

	@Test
	@DisplayName("Withdrawing down to exactly the minimum balance should work")
	public void testWithdrawDownToMinBalance() throws InvalidAmountException, InvalidDateException
	{
		long amount = HUNDRED_CHF - PromoYouthSavingsAccount.MIN_BALANCE;

		long newBalance = account.withdraw(Scheduled.now(), amount);

		assertEquals(PromoYouthSavingsAccount.MIN_BALANCE, newBalance);
		assertEquals(PromoYouthSavingsAccount.MIN_BALANCE, account.getBalance());
	}

	@Test
	@DisplayName("Withdrawing one Millirappen below the minimum balance should throw")
	public void testWithdrawBelowMinBalance()
	{
		long amount = HUNDRED_CHF - PromoYouthSavingsAccount.MIN_BALANCE + 1;

		assertThrows(InvalidAmountException.class, () -> account.withdraw(Scheduled.now(), amount));
	}

	@Test
	@DisplayName("A rejected withdrawal should leave the balance untouched")
	public void testRejectedWithdrawLeavesBalance()
	{
		assertThrows(InvalidAmountException.class, () -> account.withdraw(Scheduled.now(), HUNDRED_CHF));

		assertEquals(HUNDRED_CHF, account.getBalance());
	}

	@Test
	@DisplayName("Withdrawing from an account below the minimum balance should throw")
	public void testWithdrawFromSmallAccount() throws InvalidAmountException, InvalidDateException
	{
		var small = new PromoYouthSavingsAccount("Y-1001");
		small.deposit(Scheduled.now(), PromoYouthSavingsAccount.MIN_BALANCE);

		assertThrows(InvalidAmountException.class, () -> small.withdraw(Scheduled.now(), 1));
		assertEquals(PromoYouthSavingsAccount.MIN_BALANCE, small.getBalance());
	}

	@Test
	@DisplayName("Inherited validation: a non-positive amount should still throw")
	public void testWithdrawInvalidAmount()
	{
		assertThrows(InvalidAmountException.class, () -> account.withdraw(Scheduled.now(), 0));
		assertThrows(InvalidAmountException.class, () -> account.withdraw(Scheduled.now(), -1));
	}

	@Test
	@DisplayName("A PromoYouthSavingsAccount is usable as a SavingsAccount and an Account")
	public void testIsSavingsAccount()
	{
		SavingsAccount savings = account;
		Account plain = account;

		assertEquals(HUNDRED_CHF, savings.getBalance());
		assertEquals(HUNDRED_CHF, plain.getBalance());
	}
}
