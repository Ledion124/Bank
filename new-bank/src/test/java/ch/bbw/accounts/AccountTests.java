package ch.bbw.accounts;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


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

    }

    /**
     * Testet das Einzahlen auf ein Konto.
     */
    @Test
    @DisplayName("Simple deposit on SavingsAccount should work")
    public void testDeposit() {
        fail("ToDo");
    }

    /**
     * Testet das Abheben von einem Konto.
     */
    @Test
    @DisplayName("Simple withdraw from SalaryAccount should work")
    public void testWithdraw() {
        fail("ToDo");
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
    public void testPrint() {
        fail("ToDo");
    }

    /**
     * Experimente mit print(year,month).
     */
    @Test
    @DisplayName("Not a real test, it prints a list of bookings")
    public void testMonthlyPrint() {
        fail("ToDo");
    }

}
