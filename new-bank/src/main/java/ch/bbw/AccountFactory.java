package ch.bbw;

import ch.bbw.accounts.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Erstellt alle Konten der Bank (Factory Pattern).
 *
 * <p>
 * Die Factory ist der einzige Ort, der entscheidet, welche konkrete
 * {@link Account}-Unterklasse erstellt wird und welche Kontonummer sie bekommt.
 * Die {@link Bank} kennt nur die abstrakte Klasse {@link Account} und muss
 * nicht geändert werden, wenn ein Kontotyp dazukommt oder sich ändert.
 * </p>
 *
 * <p>
 * <b>Designentscheid:</b>
 * </p>
 * <ul>
 *     <li><b>Simple Factory statt Factory Method:</b> Eine einzige Klasse mit
 *     einem {@code switch} über {@link AccountType} genügt für drei Produkte.
 *     Eine eigene Creator-Unterklasse pro Kontotyp (Factory-Method-Pattern)
 *     wäre hier mehr Aufwand als Nutzen.</li>
 *     <li><b>Benannte Methoden und {@code create(type)}:</b> Die {@link Bank}
 *     nutzt die drei benannten Methoden, weil sie lesbar sind und Sparkonten
 *     keine sinnlose Kreditlimite brauchen. {@link #create(AccountType, long)}
 *     ist der allgemeine Einstieg, z.B. um über alle Typen zu iterieren.</li>
 *     <li><b>Rückgabetyp {@link Account}:</b> Die Methoden liefern bewusst den
 *     abstrakten Typ, damit Aufrufer nicht von konkreten Klassen abhängen.</li>
 *     <li><b>Ein gemeinsamer, thread-sicherer Zähler:</b> Die Nummer ist über
 *     alle Typen eindeutig (S-1000, Y-1001, P-1002). {@link AtomicLong}
 *     verhindert doppelte Nummern, wenn mehrere Threads gleichzeitig Konten
 *     erstellen.</li>
 * </ul>
 *
 * @author Louis Engel
 * @version 2.0
 */
public class AccountFactory {
    /**
     * Nächste freie Kontonummer, gemeinsam für alle Kontotypen.
     */
    private final AtomicLong nextAccountId;

    /**
     * Erstellt eine neue Factory. Die erste Kontonummer ist 1000.
     */
    public AccountFactory() {
        this.nextAccountId = new AtomicLong(1000);
    }

    /**
     * Erstellt ein neues Promo-Jugendsparkonto.
     *
     * @return das neue Konto, Kontonummer mit Präfix "Y-"
     */
    public Account createPromoYouthSavingsAccount() {
        String id = nextId(AccountType.PROMO_YOUTH);

        return new PromoYouthSavingsAccount(id);
    }

    /**
     * Erstellt ein neues Lohnkonto mit der gegebenen Kreditlimite.
     *
     * @param creditLimit die Kreditlimite, 0 oder negativ (z.B. -50000)
     * @return das neue Konto, Kontonummer mit Präfix "P-"
     * @throws IllegalArgumentException wenn die Kreditlimite positiv ist
     */
    public Account createSalaryAccount(long creditLimit) {
        String id = nextId(AccountType.SALARY);

        return new SalaryAccount(id, creditLimit);
    }

    /**
     * Erstellt ein neues Sparkonto.
     *
     * @return das neue Konto, Kontonummer mit Präfix "S-"
     */
    public Account createSavingsAccount(){
        String id = nextId(AccountType.SAVINGS);

        return new SavingsAccount(id);
    }

    /**
     * Erstellt ein neues Konto des gegebenen Typs ohne Kreditlimite.
     * Ein Lohnkonto bekommt die Kreditlimite 0.
     *
     * @param type der Kontotyp
     * @return das neue Konto
     * @throws IllegalArgumentException wenn {@code type} {@code null} ist
     */
    public Account create(AccountType type) {
        return create(type, 0);
    }

    /**
     * Erstellt ein neues Konto des gegebenen Typs.
     * Die Kreditlimite wird nur beim Lohnkonto verwendet.
     *
     * @param type        der Kontotyp
     * @param creditLimit die Kreditlimite für ein Lohnkonto, 0 oder negativ;
     *                    bei anderen Typen ignoriert
     * @return das neue Konto
     * @throws IllegalArgumentException wenn {@code type} {@code null} ist oder
     *                                  die Kreditlimite eines Lohnkontos positiv ist
     */
    public Account create(AccountType type, long creditLimit) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }

        return switch (type) {
            case SAVINGS -> createSavingsAccount();
            case PROMO_YOUTH -> createPromoYouthSavingsAccount();
            case SALARY -> createSalaryAccount(creditLimit);
        };
    }

    /**
     * Vergibt die nächste Kontonummer für den gegebenen Typ.
     *
     * @param type der Kontotyp, liefert das Präfix
     * @return die neue Kontonummer, z.B. "S-1000"
     */
    private String nextId(AccountType type) {
        return type.getPrefix() + "-" + nextAccountId.getAndIncrement();
    }
}
