package ch.bbw.accounts;


import ch.bbw.Scheduled;
import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests für die Klasse Account.
 *
 * @author luigicavuoti
 * @version 2.0
 */
public class AccountTests {
    private Account account;

    public void setUp() {
        account = new SavingsAccount("1234567890");

    }

    /**
     * Tested die Initialisierung eines Kontos.
     */
    @Test
    @DisplayName("Simple constructor-Test for SavingsAccount should work")
    public void testInit() {
       // simple constructor test for SavingsAccount
        account = new SavingsAccount("1234567890");
        assertEquals("1234567890", account.getId());

        // ein neues Konto hat Saldo 0 und keine Buchungen
        assertEquals(0, account.getBalance());
        assertEquals(0, account.getBookings().size());
    }

    /**
     * Testet das Einzahlen auf ein Konto.
     */
    @Test
    @DisplayName("Simple deposit on SavingsAccount should work")
    public void testDeposit() throws InvalidDateException, InvalidAmountException {
        Account account = new SavingsAccount("1234");

        account.deposit(Scheduled.now(), 1000, "text");

        assertEquals(1, account.getBookings().size());
        assertEquals(1000, account.getBookings().get(0).amount());
        assertEquals("text", account.getBookings().get(0).text());

        // die Buchungsliste darf von aussen nicht veränderbar sein
        assertThrows(UnsupportedOperationException.class, () -> account.getBookings().clear());
        assertThrows(InvalidAmountException.class, () -> account.deposit(Scheduled.now(),0,"text") );
        assertEquals(1, account.getBookings().size());
        assertEquals(1000, account.getBalance());
    }

    /**
     * Testet das Abheben von einem Konto.
     */
    @Test
    @DisplayName("Simple withdraw from SavingsAccount should work")
    public void testWithdraw() throws InvalidDateException, InvalidAmountException {
        Account account = new SavingsAccount("1234");
        account.deposit(Scheduled.now(), 100, "text");

        account.withdraw(Scheduled.now(), 100, "text");
        assertEquals(2, account.getBookings().size());
        assertEquals(-100,account.getBookings().get(1).amount());
        assertEquals("text", account.getBookings().get(1).text());

        long sum = account.getBookings().get(0).amount() + account.getBookings().get(1).amount();
        assertEquals(sum, account.getBalance());
    }

    @Test
    @DisplayName("TreeMap of two Account-References should work")
    public void testReferences() {
        fail("ToDo");
    }

    @Test
    @DisplayName("TreeMap of two Account-References should work")
    public void testCanTransact() {
        fail("ToDo");
    }

    /**
     * Experimente mit print().
     */
    @Test
    @DisplayName("Not a real test, it prints a list of bookings")
    public void testPrint() throws InvalidDateException, InvalidAmountException {
        Account account = new SavingsAccount("1234");
        account.deposit(Scheduled.now(), 1000, "Lohn");
        account.withdraw(Scheduled.now(), 100, "Miete");

        String statement = account.printStatement();
        System.out.println(statement);

        // eine Zeile pro Buchung: beide Texte müssen im Auszug stehen
        assertTrue(statement.contains("Lohn"));
        assertTrue(statement.contains("Miete"));
    }

    /**
     * Experimente mit print(year,month).
     */
    @Test
    @DisplayName("Not a real test, it prints a list of bookings")
    public void testMonthlyPrint() throws InvalidDateException, InvalidAmountException {
        Account account = new SavingsAccount("1234");
        account.deposit(Scheduled.now(), 1000, "Lohn");

        LocalDate today = LocalDate.now();
        String thisMonth = account.printStatement(today.getYear(), today.getMonthValue());
        String otherMonth = account.printStatement(2000, 1);
        System.out.println(thisMonth);

        // nur die Buchungen des gewählten Monats erscheinen
        assertTrue(thisMonth.contains("Lohn"));
        assertFalse(otherMonth.contains("Lohn"));
    }



}
