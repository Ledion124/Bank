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

    /**
     * create(SAVINGS) liefert ein Sparkonto mit Präfix "S-".
     */
    @Test
    @DisplayName("create(SAVINGS) returns a SavingsAccount with prefix S-")
    public void testCreateWithTypeSavings() {
        Account account = factory.create(AccountType.SAVINGS);

        assertInstanceOf(SavingsAccount.class, account);
        assertTrue(account.getId().startsWith("S-"));
    }

    /**
     * create(PROMO_YOUTH) liefert ein Jugendsparkonto mit Präfix "Y-".
     */
    @Test
    @DisplayName("create(PROMO_YOUTH) returns a PromoYouthSavingsAccount with prefix Y-")
    public void testCreateWithTypePromoYouth() {
        Account account = factory.create(AccountType.PROMO_YOUTH);

        assertInstanceOf(PromoYouthSavingsAccount.class, account);
        assertTrue(account.getId().startsWith("Y-"));
    }

    /**
     * create(SALARY, limit) liefert ein Lohnkonto mit Präfix "P-" und der
     * gegebenen Kreditlimite.
     */
    @Test
    @DisplayName("create(SALARY, -50000) returns a SalaryAccount with that credit limit")
    public void testCreateWithTypeSalary() {
        Account account = factory.create(AccountType.SALARY, -50_000);

        SalaryAccount salary = assertInstanceOf(SalaryAccount.class, account);
        assertTrue(salary.getId().startsWith("P-"));
        assertEquals(-50_000, salary.getCreditLimit());
    }

    /**
     * create(SALARY) ohne Limite liefert ein Lohnkonto mit Kreditlimite 0.
     */
    @Test
    @DisplayName("create(SALARY) without a limit returns a SalaryAccount with credit limit 0")
    public void testCreateWithTypeSalaryWithoutLimit() {
        Account account = factory.create(AccountType.SALARY);

        SalaryAccount salary = assertInstanceOf(SalaryAccount.class, account);
        assertEquals(0, salary.getCreditLimit());
    }

    /**
     * create(SALARY, positive Limite) wird abgelehnt.
     */
    @Test
    @DisplayName("create(SALARY, +50000) is rejected")
    public void testCreateWithTypeSalaryPositiveLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> factory.create(AccountType.SALARY, 50_000));
    }

    /**
     * Ohne Kontotyp kann kein Konto erstellt werden.
     */
    @Test
    @DisplayName("create(null) is rejected")
    public void testCreateWithoutTypeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }
}
