package ch.bbw;

import ch.bbw.accounts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für die Klasse {@link AccountFactory} (Sprint 4).
 *
 * <p>
 * Prüft den Vertrag der Factory: jede create-Methode liefert den richtigen
 * Kontotyp mit dem passenden Präfix, die Ids sind eindeutig und die
 * Kreditlimite erreicht das Lohnkonto.
 * </p>
 *
 * @author Ledion Sakaj
 * @version 1.0
 */
public class AccountFactoryTests {
    /**
     * Die zu testende Factory, vor jedem Test neu erstellt.
     */
    private AccountFactory factory;

    /**
     * Erstellt vor jedem Test eine neue Factory.
     */
    @BeforeEach
    public void setUp() {
        factory = new AccountFactory();
    }

    /**
     * createSavingsAccount liefert ein Sparkonto mit Präfix "S-".
     */
    @Test
    @DisplayName("createSavingsAccount returns a SavingsAccount with prefix S-")
    public void testCreateSavingsAccount() {
        Account account = factory.createSavingsAccount();

        assertTrue(account instanceof SavingsAccount);
        assertTrue(account.getId().startsWith("S-"));
    }

    /**
     * createPromoYouthSavingsAccount liefert ein Jugendsparkonto mit Präfix "Y-".
     */
    @Test
    @DisplayName("createPromoYouthSavingsAccount returns a PromoYouthSavingsAccount with prefix Y-")
    public void testCreatePromoYouthSavingsAccount() {
        Account account = factory.createPromoYouthSavingsAccount();

        assertTrue(account instanceof PromoYouthSavingsAccount);
        assertTrue(account.getId().startsWith("Y-"));
    }

    /**
     * createSalaryAccount liefert ein Lohnkonto mit Präfix "P-".
     */
    @Test
    @DisplayName("createSalaryAccount returns a SalaryAccount with prefix P-")
    public void testCreateSalaryAccount() {
        Account account = factory.createSalaryAccount(-50_000);

        assertTrue(account instanceof SalaryAccount);
        assertTrue(account.getId().startsWith("P-"));
    }

    /**
     * Regressionstest: Die Kreditlimite wird an das Lohnkonto weitergegeben
     * und nicht mehr weggeworfen.
     */
    @Test
    @DisplayName("createSalaryAccount passes the credit limit to the SalaryAccount")
    public void testCreditLimitReachesSalaryAccount() {
        Account account = factory.createSalaryAccount(-50_000);

        // Regressionstest: früher wurde creditLimit einfach weggeworfen
        SalaryAccount salary = assertInstanceOf(SalaryAccount.class, account);
        assertEquals(-50_000, salary.getCreditLimit());
    }

    /**
     * Eine positive Kreditlimite wird abgelehnt.
     */
    @Test
    @DisplayName("createSalaryAccount with a positive credit limit is rejected")
    public void testPositiveCreditLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> factory.createSalaryAccount(50_000));
    }

    /**
     * Über 3000 erstellte Konten aller Typen gibt es keine doppelte Id.
     */
    @Test
    @DisplayName("Ids are unique over 1000 creations of all types")
    public void testIdsAreUnique() {
        Set<String> ids = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            ids.add(factory.createSavingsAccount().getId());
            ids.add(factory.createPromoYouthSavingsAccount().getId());
            ids.add(factory.createSalaryAccount(0).getId());
        }

        assertEquals(3000, ids.size());
    }
}
