import ch.bbw.Bank;
import ch.bbw.Scheduled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Tests f�r die Klasse 'Bank'.
 *
 * @author luigicavuoti
 * @version 1.0
 */
public class BankTests {
    /**
     * Testet die Initialisierung einer Bank.
     */
    @Test
    @DisplayName("Test the initialization of a new bank")
    public void testInit() {
        Bank bank;
        bank = new Bank();

        assertEquals(0, bank.getBalance());
    }

    @Test
    @DisplayName("Constructor Test, creates two SavingsAccounts")
    public void testCreate() {
        fail("ToDo");
    }

    @Test
    @DisplayName("Simple deposit on Bank-SavingsAccount should work")
    public void testSimpleDeposit() {
        fail("ToDo");
    }

    @Test
    @DisplayName("Simple withdraw on Bank-SavingsAccount should work")
    public void testWithdraw() {
        fail("ToDo");
    }

    /**
     * Experimente mit print().
     */
    @Test
    @DisplayName("Not a real Test, it prints the Balance of the account")
    public void testPrint() {
        fail("ToDo");
    }

    /**
     * Experimente mit print(year, month).
     */
    @Test
    @DisplayName("Not a real Test, it prints the Balance of the account")
    public void testMonthlyPrint() {
        fail("ToDo");
    }

    /**
     * Testet den Gesamtkontostand der Bank.
     */
    @Test
    @DisplayName("Tests the balance of two SalaryAccounts from the Bank")
    public void testBalance() {
        fail("ToDo");
    }

    /**
     * Tested die Ausgabe der "top 5" konten.
     */
    @Test
    @DisplayName("Tests the List of the top 5 balances")
    public void testTop5() {
        fail("ToDo");
    }

    /**
     * Tested die Ausgabe der "top 5" konten.
     */
    @Test
    @DisplayName("Tests the List of the bottom 5 balances")
    public void testBottom5() {
        fail("ToDo");
    }
}
