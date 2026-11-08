package ch.bbw;


import ch.bbw.accounts.Account;
import ch.bbw.exceptions.*;


import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Die Bank. Verwaltet alle Konten und ist ein <b>Singleton</b> (Sprint 5).
 *
 * <h2>Umsetzung</h2>
 * <p>
 * Die einzige Instanz liefert {@link #getInstance()}. Der Konstruktor ist nicht
 * öffentlich, die {@link AccountFactory} wird über den Konstruktor hineingegeben
 * statt in der Bank erstellt.
 * </p>
 *
 * <h2>Vergleich der drei Varianten</h2>
 * <table border="1">
 *     <caption>Singleton-Varianten</caption>
 *     <tr><th>Variante</th><th>Thread-sicher?</th><th>Lazy?</th><th>Kosten pro Aufruf</th></tr>
 *     <tr><td>(a) Eager: {@code static final INSTANCE = new Bank()}</td>
 *         <td>ja, die JVM initialisiert statische Felder genau einmal</td>
 *         <td>nein, erstellt beim Laden der Klasse</td>
 *         <td>nur ein Feldzugriff</td></tr>
 *     <tr><td>(b) Lazy mit {@code synchronized getInstance()}</td>
 *         <td>ja</td>
 *         <td>ja</td>
 *         <td>jeder Aufruf holt ein Lock, auch lange nachdem die Instanz existiert</td></tr>
 *     <tr><td>(c) Initialization-on-demand Holder</td>
 *         <td>ja, die JVM initialisiert die Holder-Klasse genau einmal</td>
 *         <td>ja, {@code Holder} wird erst beim ersten {@code getInstance()} geladen</td>
 *         <td>nur ein Feldzugriff, kein Lock</td></tr>
 * </table>
 *
 * <p>
 * <b>Gewählt: (c) Holder.</b> Sie ist thread-sicher und lazy wie (b), kostet
 * pro Aufruf aber nicht mehr als (a). Die Thread-Sicherheit kommt von der JVM,
 * nicht von eigenem Lock-Code, der falsch sein könnte.
 * </p>
 *
 * <p>
 * <b>Warum Double-Checked Locking ohne {@code volatile} falsch ist:</b>
 * {@code instance = new Bank()} besteht aus drei Schritten: Speicher
 * reservieren, Konstruktor ausführen, Referenz ins Feld schreiben. Ohne
 * {@code volatile} darf die JVM (bzw. die CPU) diese Schritte umordnen und
 * die Referenz schreiben, bevor der Konstruktor fertig ist. Ein zweiter
 * Thread sieht beim ersten, nicht synchronisierten Check dann
 * {@code instance != null} und benutzt eine halb erstellte Bank, z.B. mit
 * {@code accounts == null}. Erst {@code volatile} verbietet diese Umordnung
 * (happens-before).
 * </p>
 *
 * <h2>Soll die Bank überhaupt ein Singleton sein?</h2>
 * <p><b>Vorteile:</b></p>
 * <ul>
 *     <li>Es gibt fachlich genau eine Bank; der Code bildet das ab.</li>
 *     <li>Ein globaler Zugriffspunkt: Die Bank muss nicht durch jede Schicht
 *     als Parameter durchgereicht werden.</li>
 *     <li>Der Kontonummern-Zähler der {@link AccountFactory} existiert nur
 *     einmal, es entstehen keine doppelten Kontonummern durch eine zweite Bank.</li>
 * </ul>
 * <p><b>Nachteile:</b></p>
 * <ul>
 *     <li><b>Versteckte Abhängigkeit:</b> Eine Methode, die intern
 *     {@code Bank.getInstance()} aufruft, braucht eine Bank, sagt es in ihrer
 *     Signatur aber nicht.</li>
 *     <li><b>Globaler veränderbarer Zustand:</b> Jeder Code im Prozess kann auf
 *     jedes Konto einzahlen oder abheben.</li>
 *     <li><b>Testbarkeit:</b> Man kann die Bank nicht durch ein Test-Double
 *     ersetzen und keine zwei unabhängigen Szenarien parallel laufen lassen.
 *     Die Tests brauchen den Workaround {@link #reset()}.</li>
 *     <li><b>Lebensdauer:</b> Die Instanz lebt, bis die JVM endet; man kann sie
 *     nicht abbauen.</li>
 *     <li><b>Erstellung statt Design:</b> "Es gibt eine Bank" ist eine Aussage
 *     über die Welt. "Es gibt genau ein {@code Bank}-Objekt in einem statischen
 *     Feld" ist eine viel stärkere Behauptung. Zudem gilt sie nur pro JVM: Laufen
 *     zwei Instanzen des Service hinter einem Load Balancer, gibt es wieder zwei Banken.</li>
 * </ul>
 *
 * <p>
 * <b>Gegenvorschlag (umgesetzt):</b> Der Singleton bleibt, aber der Code hängt
 * nicht von ihm ab. Die Factory kommt über den Konstruktor
 * {@link #Bank(AccountFactory)}, Tests bauen damit ihre eigene Bank, und
 * {@code getInstance()} soll nur am äussersten Einstiegspunkt aufgerufen werden
 * (heute: Tests, ab Sprint 6: die Spring-Konfiguration, Composition Root).
 * Die {@link AccountFactory} machen wir bewusst <i>nicht</i> zum Singleton:
 * Sie gehört der Bank, und über die Bank gibt es sie ohnehin nur einmal.
 * </p>
 *
 * @author luigicavuoti, lro@gmx.ch, Silvan Castelberg
 * @version 3.0
 */
public class Bank {
    /**
     * Liste aller Konti.
     */
    private final TreeMap<String, Account> accounts;
    /**
     * Erstellt die Konten, wird über den Konstruktor hineingegeben.
     */
    private final AccountFactory accountFactory;

    /**
     * Hält die einzige Instanz (Initialization-on-demand Holder).
     * Die JVM lädt diese Klasse erst beim ersten Aufruf von {@link #getInstance()}
     * und garantiert, dass {@code INSTANCE} genau einmal und thread-sicher erstellt wird.
     */
    private static class Holder {
        /**
         * Die einzige Bank der Anwendung.
         */
        static final Bank INSTANCE = new Bank(new AccountFactory());
    }

    /**
     * Initialisiert eine neue Bank mit der gegebenen Factory.
     *
     * <p>
     * Paketintern statt öffentlich: Die Anwendung holt die Bank über
     * {@link #getInstance()}, Tests im Package {@code ch.bbw} können aber
     * eine eigene, unabhängige Bank mit einer bekannten Factory bauen.
     * </p>
     *
     * @param accountFactory die Factory, die die Konten erstellt
     * @throws IllegalArgumentException falls {@code accountFactory} {@code null} ist
     */
    Bank(AccountFactory accountFactory) {
        if (accountFactory == null)
            throw new IllegalArgumentException("accountFactory must not be null");

        this.accounts = new TreeMap<String, Account>();
        this.accountFactory = accountFactory;
    }

    /**
     * Gibt die einzige Bank-Instanz der Anwendung zurück.
     *
     * @return die Bank, bei jedem Aufruf dieselbe Instanz
     */
    public static Bank getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Entfernt alle Konten aus der Bank-Instanz. <b>Nur für Tests.</b>
     *
     * <p>
     * <b>Workaround, kein sauberes Design:</b> Seit {@code Bank} ein Singleton ist,
     * teilen sich alle Tests dieselbe Instanz. Ohne {@code reset()} hängt das
     * Ergebnis eines Tests davon ab, welche Tests vorher gelaufen sind. Eine
     * öffentliche Methode, die nur existiert, damit Tests einen Designentscheid
     * rückgängig machen können, ist ein <b>Code Smell</b>: Jeder Code in der
     * Anwendung könnte damit alle Konten löschen. Die bessere Lösung ist der
     * Konstruktor {@link #Bank(AccountFactory)}: Ein Test baut seine eigene Bank.
     * </p>
     *
     * <p>
     * JUnit führt die Tests heute nacheinander aus (keine
     * {@code junit-platform.properties} mit Parallelausführung). Liefen die Tests
     * parallel, würde ein {@code reset()} in einem Test die Konten eines anderen,
     * gleichzeitig laufenden Tests löschen; dieser Workaround hilft dann nicht mehr.
     * </p>
     */
    public static void reset() {
        getInstance().accounts.clear();
    }

    /**
     * Erzeugt ein neues Sparkonto
     *
     * @return die neue Kontonummer
     */
    public String createSavingsAccount() {
        var account = accountFactory.createSavingsAccount();
        var id = account.getId();

        accounts.put(id, account);

        return id;
    }

    /**
     * Erzeugt ein neues Promo-Jugendsparkonto
     *
     * @return die neue Kontonummer
     */
    public String createPromoYouthSavingsAccount() {
        var account = accountFactory.createPromoYouthSavingsAccount();
        var id = account.getId();

        accounts.put(id, account);

        return id;
    }

    /**
     * Erzeugt ein neues Lohnkonto
     *
     * @param creditLimit Kreditlimite (negative Zahl)
     * @return String die neue Kontonummer
     */
    public String createSalaryAccount(long creditLimit) throws IllegalArgumentException {
        var account = accountFactory.createSalaryAccount(creditLimit);
        var id = account.getId();

        accounts.put(id, account);

        return id;

    }

    /**
     * Gibt den Kontostand der Bank zurück.
     *
     * @return long der Kontostand der Bank
     */
    public long getBalance() {
        return accounts
                .values()
                .stream()
                .mapToLong(Account::getBalance)
                .sum();
    }

    /**
     * Gibt den Kontostand des Kontos mit der gegebenen
     * Kontonummer zurück.
     *
     * @param id die Kontonummer
     * @return long der Kontostand des Kontos
     * @throws InvalidAmountException falls es kein Konto mit dieser Kontonummer gibt
     */
    public long getBalance(String id) throws InvalidAmountException {
        return getAccount(id).getBalance();
    }

    /**
     * Zahlt den gegebenen Betrag auf das Konto mit
     * der gegebenen Kontonummer ein. Als Buchungstext wird "Einzahlung" verwendet.
     *
     * @param id     die Kontonummer
     * @param date   das Transaktionsdatum
     * @param amount der einzuzahlende Betrag
     * @throws InvalidAmountException falls das Konto nicht existiert oder der Betrag nicht grösser als 0 ist
     * @throws InvalidDateException   falls das Datum in der Vergangenheit liegt
     */
    public void deposit(String id, Scheduled date,
                        long amount) throws InvalidAmountException, InvalidDateException {
        var account = getAccount(id);

        account.deposit(date, amount);
    }

    /**
     * Zahlt den gegebenen Betrag mit einem Buchungstext auf das Konto
     * mit der gegebenen Kontonummer ein.
     *
     * @param id     die Kontonummer
     * @param date   das Transaktionsdatum
     * @param amount der einzuzahlende Betrag
     * @param text   der Buchungstext
     * @throws InvalidAmountException falls das Konto nicht existiert oder der Betrag nicht grösser als 0 ist
     * @throws InvalidDateException   falls das Datum in der Vergangenheit liegt
     */
    public void deposit(String id, Scheduled date, long amount, String text)
            throws InvalidAmountException, InvalidDateException {
        var account = getAccount(id);

        account.deposit(date, amount, text);
    }

    /**
     * Hebt den gegebenen Betrag vom Konto mit
     * der gegebenen Kontonummer ab. Als Buchungstext wird "Auszahlung" verwendet.
     *
     * @param id     die Kontonummer
     * @param date   das Transaktionsdatum
     * @param amount der abzuhebende Betrag
     * @return der neue Saldo des Kontos
     * @throws InvalidAmountException falls das Konto nicht existiert, der Betrag nicht grösser als 0 ist
     *                                oder die Regel des Kontotyps verletzt wird (z.B. Überziehen)
     * @throws InvalidDateException   falls das Datum in der Vergangenheit liegt
     */
    public long withdraw(String id, Scheduled date,
                            long amount) throws InvalidAmountException, InvalidDateException {
        var account = getAccount(id);

        return account.withdraw(date, amount);
    }

    /**
     * Hebt den gegebenen Betrag mit einem Buchungstext vom Konto
     * mit der gegebenen Kontonummer ab.
     *
     * @param id     die Kontonummer
     * @param date   das Transaktionsdatum
     * @param amount der abzuhebende Betrag
     * @param text   der Buchungstext
     * @return der neue Saldo des Kontos
     * @throws InvalidAmountException falls das Konto nicht existiert, der Betrag nicht grösser als 0 ist
     *                                oder die Regel des Kontotyps verletzt wird (z.B. Überziehen)
     * @throws InvalidDateException   falls das Datum in der Vergangenheit liegt
     */
    public long withdraw(String id, Scheduled date, long amount, String text)
            throws InvalidAmountException, InvalidDateException {
        var account = getAccount(id);

        return account.withdraw(date, amount, text);
    }

    /**
     * Gibt die (höchstens) fünf Konten mit dem höchsten Saldo zurück,
     * absteigend sortiert.
     *
     * @return bis zu fünf Konten, das reichste zuerst; leer, wenn die Bank keine Konten hat
     */
    public List<Account> top5HighestBalances() {
        return accounts.values()
                .stream()
                .sorted(Comparator.comparingLong(Account::getBalance).reversed())
                .limit(5)
                .toList();
    }

    /**
     * Gibt die (höchstens) fünf Konten mit dem tiefsten Saldo zurück,
     * aufsteigend sortiert. Überzogene Lohnkonten (negativer Saldo) stehen zuerst.
     *
     * @return bis zu fünf Konten, das ärmste zuerst; leer, wenn die Bank keine Konten hat
     */
    public List<Account> top5LowestBalances() {
        return accounts.values()
                .stream()
                .sorted(Comparator.comparingLong(Account::getBalance))
                .limit(5)
                .toList();
    }

    /**
     * Sucht das Konto mit der gegebenen Kontonummer.
     *
     * @param id die Kontonummer
     * @return das Konto
     * @throws InvalidAmountException falls es kein Konto mit dieser Kontonummer gibt
     */
    public Account getAccount(String id) throws InvalidAmountException {

        var account = Optional.ofNullable(accounts.get(id));

        if (account.isEmpty()) {
            throw new InvalidAmountException("Invalid bank id");
        }

        return account.get();
    }

}
