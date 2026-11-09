package ch.bbw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für das Enum {@link AccountType} (Sprint 4).
 *
 * <p>
 * Prüft, dass jeder Kontotyp sein Präfix für die Kontonummer kennt und dass
 * die Präfixe eindeutig sind. So kann das Präfix nicht mehr vom Typ
 * "wegdriften", wie es mit verstreuten String-Literalen möglich war.
 * </p>
 *
 * @author Louis Engel
 * @version 1.0
 */
public class AccountTypeTests {

    /**
     * Das Sparkonto hat das Präfix "S".
     */
    @Test
    @DisplayName("SAVINGS has prefix S")
    public void testSavingsPrefix() {
        assertEquals("S", AccountType.SAVINGS.getPrefix());
    }

    /**
     * Das Promo-Jugendsparkonto hat das Präfix "Y".
     */
    @Test
    @DisplayName("PROMO_YOUTH has prefix Y")
    public void testPromoYouthPrefix() {
        assertEquals("Y", AccountType.PROMO_YOUTH.getPrefix());
    }

    /**
     * Das Lohnkonto hat das Präfix "P".
     */
    @Test
    @DisplayName("SALARY has prefix P")
    public void testSalaryPrefix() {
        assertEquals("P", AccountType.SALARY.getPrefix());
    }

    /**
     * Es gibt genau drei Produkte, und keine zwei Typen teilen sich ein Präfix.
     */
    @Test
    @DisplayName("There are exactly three account types with unique prefixes")
    public void testPrefixesAreUnique() {
        Set<String> prefixes = new HashSet<>();

        for (AccountType type : AccountType.values()) {
            prefixes.add(type.getPrefix());
        }

        assertEquals(3, AccountType.values().length);
        assertEquals(3, prefixes.size());
    }
}
