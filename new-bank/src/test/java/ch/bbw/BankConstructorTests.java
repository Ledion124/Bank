package ch.bbw;

import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für den paketinternen Konstruktor {@code Bank(AccountFactory)} (Sprint 5).
 *
 * <p>
 * Der Singleton bleibt der Weg für die Anwendung ({@link Bank#getInstance()}).
 * Tests im Package {@code ch.bbw} können aber eine eigene, unabhängige Bank mit
 * einer eigenen {@link AccountFactory} bauen. So hängen sie nicht vom globalen
 * Zustand des Singletons ab und brauchen kein {@code reset()}.
 * </p>
 *
 * @author Silvan Castelberg
 * @version 1.0
 */
public class BankConstructorTests {
    /**
     * Die Bank verwendet die hineingegebene Factory: Eine neue Factory beginnt
     * bei 1000, also bekommt das erste Konto die Nummer "S-1000".
     */
    @Test
    @DisplayName("Bank uses the injected AccountFactory")
    public void testInjectedFactory() {
        Bank bank = new Bank(new AccountFactory());

        assertEquals("S-1000", bank.createSavingsAccount());
        assertEquals("P-1001", bank.createSalaryAccount(-1000));
    }

    /**
     * Eine Factory {@code null} wird abgelehnt.
     */
    @Test
    @DisplayName("Bank rejects a null AccountFactory")
    public void testNullFactory() {
        assertThrows(IllegalArgumentException.class, () -> new Bank(null));
    }

    /**
     * Zwei über den Konstruktor gebaute Banken sind unabhängig voneinander
     * und unabhängig vom Singleton.
     */
    @Test
    @DisplayName("Banks built with the constructor are independent of each other and of the singleton")
    public void testIndependentBanks() throws InvalidAmountException, InvalidDateException {
        Bank first = new Bank(new AccountFactory());
        Bank second = new Bank(new AccountFactory());

        String id = first.createSavingsAccount();
        first.deposit(id, Scheduled.now(), 1000);

        assertEquals(1000, first.getBalance());
        assertEquals(0, second.getBalance());
        assertNotSame(first, Bank.getInstance());
        assertNotSame(first, second);
    }
}
