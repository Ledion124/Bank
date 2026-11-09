import ch.bbw.Bank;
import ch.bbw.Scheduled;
import ch.bbw.accounts.Account;
import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests für die Klasse 'Bank'.
 *
 * <p>
 * Seit Sprint 5 ist {@link Bank} ein Singleton: Alle Tests teilen sich dieselbe
 * Instanz. Damit die Tests trotzdem unabhängig voneinander und von ihrer
 * Reihenfolge sind, leert {@link #setUp()} die Bank vor jedem Test mit
 * {@link Bank#reset()}. Das ist ein Workaround, siehe JavaDoc von {@code reset()}.
 * </p>
 *
 * @author luigicavuoti, Ledion Sakaj, Silvan Castelberg
 * @version 3.0
 */
public class BankTests {
    /**
     * Leert die gemeinsame Bank-Instanz vor jedem Test (Workaround für den Singleton).
     */
    @BeforeEach
    public void setUp() {
        Bank.reset();
    }

    /**
     * getInstance() liefert bei jedem Aufruf dieselbe Instanz.
     */
    @Test
    @DisplayName("getInstance returns always the same instance")
    public void testSingletonIdentity() {
        assertSame(Bank.getInstance(), Bank.getInstance());
    }

    /**
     * Die Bank hat keinen öffentlichen Konstruktor, {@code new Bank()} ist von aussen nicht möglich.
     */
    @Test
    @DisplayName("Bank has no public constructor")
    public void testNoPublicConstructor() {
        assertThrows(NoSuchMethodException.class, () -> Bank.class.getConstructor());
        assertEquals(0, Bank.class.getConstructors().length);
    }

    /**
     * Viele Threads rufen gleichzeitig getInstance() auf und bekommen alle dieselbe Instanz.
     */
    @Test
    @DisplayName("getInstance is thread-safe: concurrent calls return the same instance")
    public void testConcurrentGetInstance() throws InterruptedException {
        int threads = 50;
        Set<Bank> instances = ConcurrentHashMap.newKeySet();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                start.await();
                instances.add(Bank.getInstance());
                return null;
            });
        }
        start.countDown();
        pool.shutdown();

        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        assertEquals(1, instances.size());
        assertSame(Bank.getInstance(), instances.iterator().next());
    }

    /**
     * Isolation, Teil 1: Dieser Test verändert die gemeinsame Bank.
     * Zusammen mit {@link #testIsolationEmptyBank()} muss er in jeder Reihenfolge grün sein.
     */
    @Test
    @DisplayName("Isolation part 1: a test that changes the shared bank")
    public void testIsolationChangesBank() throws InvalidAmountException, InvalidDateException {
        Bank bank = Bank.getInstance();
        String id = bank.createSavingsAccount();

        bank.deposit(id, Scheduled.now(), 1000);

        assertEquals(1000, bank.getBalance());
    }

    /**
     * Isolation, Teil 2: Die Bank ist zu Beginn jedes Tests leer,
     * auch wenn {@link #testIsolationChangesBank()} vorher gelaufen ist.
     */
    @Test
    @DisplayName("Isolation part 2: the shared bank is empty at the start of every test")
    public void testIsolationEmptyBank() {
        Bank bank = Bank.getInstance();

        assertEquals(0, bank.getBalance());
        assertTrue(bank.top5HighestBalances().isEmpty());
    }

    /**
     * Testet die Initialisierung einer Bank.
     */
    @Test
    @DisplayName("Test the initialization of a new bank")
    public void testInit() {
        Bank bank;
        bank = Bank.getInstance();

        assertEquals(0, bank.getBalance());
        assertTrue(bank.top5HighestBalances().isEmpty());
    }

    /**
     * Zwei neue Sparkonten bekommen verschiedene Kontonummern
     * und können über die Bank gefunden werden.
     */
    @Test
    @DisplayName("Constructor Test, creates two SavingsAccounts")
    public void testCreate() throws InvalidAmountException {
        Bank bank = Bank.getInstance();

        String first = bank.createSavingsAccount();
        String second = bank.createSavingsAccount();

        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first, second);
        assertEquals(first, bank.getAccount(first).getId());
        assertEquals(second, bank.getAccount(second).getId());
        assertEquals(0, bank.getBalance(first));
    }

    /**
     * Einzahlen über die Bank, inklusive Validierung von Betrag und Datum.
     */
    @Test
    @DisplayName("Simple deposit on Bank-SavingsAccount should work")
    public void testSimpleDeposit() throws InvalidAmountException, InvalidDateException {
        Bank bank = Bank.getInstance();
        String id = bank.createSavingsAccount();

        bank.deposit(id, Scheduled.now(), 1000, "Lohn");
        assertEquals(1000, bank.getBalance(id));
        assertEquals("Lohn", bank.getAccount(id).getBookings().get(0).text());

        // ohne Text wird der Standardtext verwendet
        bank.deposit(id, Scheduled.now(), 500);
        assertEquals(1500, bank.getBalance(id));
        assertEquals("Einzahlung", bank.getAccount(id).getBookings().get(1).text());

        // Validierung: Betrag muss > 0 sein, Datum darf nicht in der Vergangenheit liegen
        assertThrows(InvalidAmountException.class, () -> bank.deposit(id, Scheduled.now(), 0));
        assertThrows(InvalidAmountException.class, () -> bank.deposit(id, Scheduled.now(), -100));
        assertThrows(InvalidDateException.class, () -> bank.deposit(id, Scheduled.now().minusDays(1), 100));
        assertEquals(1500, bank.getBalance(id));
    }

    /**
     * Abheben über die Bank, inklusive der Regel "Sparkonto darf nicht überzogen werden".
     */
    @Test
    @DisplayName("Simple withdraw on Bank-SavingsAccount should work")
    public void testWithdraw() throws InvalidAmountException, InvalidDateException {
        Bank bank = Bank.getInstance();
        String id = bank.createSavingsAccount();
        bank.deposit(id, Scheduled.now(), 1000);

        long newBalance = bank.withdraw(id, Scheduled.now(), 400, "Bancomat");
        assertEquals(600, newBalance);
        assertEquals(600, bank.getBalance(id));
        assertEquals(-400, bank.getAccount(id).getBookings().get(1).amount());

        // genau den Saldo abheben geht, einen Millirappen mehr nicht
        assertThrows(InvalidAmountException.class, () -> bank.withdraw(id, Scheduled.now(), 601));
        assertEquals(0, bank.withdraw(id, Scheduled.now(), 600));

        // Validierung: Datum darf nicht in der Vergangenheit liegen
        bank.deposit(id, Scheduled.now(), 100);
        assertThrows(InvalidDateException.class, () -> bank.withdraw(id, Scheduled.now().minusDays(1), 50));
        assertEquals(100, bank.getBalance(id));
    }

    /**
     * Experimente mit print().
     */
    @Test
    @DisplayName("Not a real Test, it prints the Balance of the account")
    public void testPrint() throws InvalidAmountException, InvalidDateException {
        Bank bank = Bank.getInstance();
        String id = bank.createSavingsAccount();
        bank.deposit(id, Scheduled.now(), 1_250_000, "Lohn");
        bank.withdraw(id, Scheduled.now(), 300_000, "Miete");

        String statement = bank.getAccount(id).printStatement();
        System.out.println("Konto " + id + ":\n" + statement);

        assertTrue(statement.contains("Lohn"));
        assertTrue(statement.contains("Miete"));
        assertEquals(950_000, bank.getBalance(id));
    }

    /**
     * Experimente mit print(year, month).
     */
    @Test
    @DisplayName("Not a real Test, it prints the Balance of the account")
    public void testMonthlyPrint() throws InvalidAmountException, InvalidDateException {
        Bank bank = Bank.getInstance();
        String id = bank.createSavingsAccount();
        bank.deposit(id, Scheduled.now(), 1_250_000, "Lohn");

        LocalDate today = LocalDate.now();
        String statement = bank.getAccount(id).printStatement(today.getYear(), today.getMonthValue());
        System.out.println("Konto " + id + ", " + today.getMonth() + ":\n" + statement);

        assertTrue(statement.contains("Lohn"));
        assertFalse(bank.getAccount(id).printStatement(2000, 1).contains("Lohn"));
    }

    /**
     * Testet den Gesamtkontostand der Bank.
     */
    @Test
    @DisplayName("Tests the balance of two SalaryAccounts from the Bank")
    public void testBalance() throws InvalidAmountException, InvalidDateException {
        Bank bank = Bank.getInstance();
        String first = bank.createSalaryAccount(-1000);
        String second = bank.createSalaryAccount(-1000);

        bank.deposit(first, Scheduled.now(), 3000, "Lohn");
        bank.deposit(second, Scheduled.now(), 2000, "Lohn");
        bank.withdraw(second, Scheduled.now(), 500, "Einkauf");

        // der Gesamtstand ist die Summe aller Konten
        assertEquals(4500, bank.getBalance());
        assertEquals(bank.getBalance(first) + bank.getBalance(second), bank.getBalance());
    }

    /**
     * Tested die Ausgabe der "top 5" konten.
     */
    @Test
    @DisplayName("Tests the List of the top 5 balances")
    public void testTop5() throws InvalidAmountException, InvalidDateException {
        Bank bank = createBankWithBalances(100, 700, 300, 500, 200, 600, 400);

        List<Account> top = bank.top5HighestBalances();

        assertEquals(5, top.size());
        assertEquals(List.of(700L, 600L, 500L, 400L, 300L), balancesOf(top));
    }

    /**
     * Tested die Ausgabe der "bottom 5" konten.
     */
    @Test
    @DisplayName("Tests the List of the bottom 5 balances")
    public void testBottom5() throws InvalidAmountException, InvalidDateException {
        Bank bank = createBankWithBalances(100, 700, 300, 500, 200, 600, 400);

        List<Account> bottom = bank.top5LowestBalances();

        assertEquals(5, bottom.size());
        assertEquals(List.of(100L, 200L, 300L, 400L, 500L), balancesOf(bottom));
    }

    /**
     * Erstellt eine Bank mit je einem Sparkonto pro Betrag.
     *
     * @param balances die Startbeträge der Konten
     * @return die Bank mit den Konten
     */
    private Bank createBankWithBalances(long... balances) throws InvalidAmountException, InvalidDateException {
        Bank bank = Bank.getInstance();
        for (long balance : balances) {
            String id = bank.createSavingsAccount();
            bank.deposit(id, Scheduled.now(), balance);
        }
        return bank;
    }

    /**
     * Gibt die Salden der Konten in derselben Reihenfolge zurück.
     *
     * @param accounts die Konten
     * @return die Salden
     */
    private List<Long> balancesOf(List<Account> accounts) {
        return accounts.stream().map(Account::getBalance).toList();
    }
}
