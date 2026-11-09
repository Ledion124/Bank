package ch.bbw;

/**
 * Die Kontotypen (Produkte), welche die Bank anbietet.
 *
 * <p>
 * Jeder Typ kennt das Präfix seiner Kontonummer. Typ und Präfix sind so an
 * einem einzigen Ort definiert und können nicht auseinanderlaufen.
 * </p>
 *
 * @author Louis Engel
 * @version 1.0
 */
public enum AccountType {
    /**
     * Sparkonto, Kontonummer beginnt mit "S-".
     */
    SAVINGS("S"),

    /**
     * Promo-Jugendsparkonto, Kontonummer beginnt mit "Y-".
     */
    PROMO_YOUTH("Y"),

    /**
     * Lohnkonto, Kontonummer beginnt mit "P-".
     */
    SALARY("P");

    /**
     * Präfix der Kontonummer.
     */
    private final String prefix;

    /**
     * Erstellt einen Kontotyp mit dem gegebenen Präfix.
     *
     * @param prefix das Präfix der Kontonummer
     */
    AccountType(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Liefert das Präfix der Kontonummer.
     *
     * @return das Präfix, z.B. "S"
     */
    public String getPrefix() {
        return prefix;
    }
}
