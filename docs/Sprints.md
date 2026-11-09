# Sprint-Dokumentation

Pro Sprint: was gemacht wurde, wer es gemacht hat und über welchen Pull Request es in
`main` gekommen ist. Die Aufträge liegen in [`docs/exercises/`](exercises/).

Ablauf in jedem Sprint: Arbeit auf dem eigenen Branch (`dev-…`) → Pull Request in den
Sprint-Branch (`sprint-N`) → Pull Request von `sprint-N` in `main`. Zu jeder Änderung werden
zuerst die Tests committet (`test(sprintN): …`), danach der Code (`feat(sprintN): …`).

## Übersicht

| Sprint | Thema | Wer | Pull Requests | Stand |
|---|---|---|---|---|
| 1 | Baseline: `Bank` und `Account` | Ledion Sakaj (Tests) | #1 | ✅ in `main` |
| 2 | Buchungen mit Text, Saldo aus Buchungen, Kontoauszug | Ledion Sakaj | #1 | ✅ in `main` |
| 3 | Kontotypen / Vererbung | Louis Engel, Silvan Castelberg | #2, #3 → `sprint-3`, #4 → `main` | ✅ in `main` |
| 4 | Factory Pattern | Ledion Sakaj (Tests), Louis Engel | #5, #6 → `sprint-4`, #7 → `main` | ✅ in `main` |
| 5 | Singleton Pattern | Silvan Castelberg | #8 (`sprint-5` → `main`) | ✅ in `main` |

## Sprint 1 – Baseline

- **Was:** Den Code der Vorlage (`Bank`, `Account`, `SavingsAccount`) mit Tests abdecken:
  einzahlen, abheben, Saldo, Validierung von Betrag und Datum.
- **Wer:** Ledion Sakaj (`BankTests`).
- **PR:** zusammen mit Sprint 2 in #1 (`dev-sakaj` → `main`).

## Sprint 2 – Buchungen

Auftrag: `02-Exercise-bookings.md`

- **Was:** Jede Ein- und Auszahlung wird als `Booking` (Datum, Betrag, Text) gespeichert.
  Der Saldo wird aus den Buchungen berechnet, `getBookings()` gibt eine unveränderbare Kopie
  zurück. Kontoauszug mit `printStatement()` und `printStatement(year, month)`.
- **Wer:** Ledion Sakaj.
- **PR:** #1 (`dev-sakaj` → `main`).

## Sprint 3 – Kontotypen / Vererbung

Auftrag: `03-Exercise-specific-accounts.md`

- **3a, Louis Engel:** `SalaryAccount extends Account` mit Kreditlimite (negativ oder 0,
  eine positive Limite wird abgelehnt). Grenztests für Sparkonto und Lohnkonto.
  PR #2 (`dev-engel` → `sprint-3`).
- **3b, Silvan Castelberg:** `PromoYouthSavingsAccount extends SavingsAccount` mit
  Mindestsaldo CHF 10.00. Abstrakte Methode `getAccountType()` in `Account`, umgesetzt in allen
  drei Kontotypen. Polymorphie-Test über eine `List<Account>`.
  PR #3 (`dev-castleberg` → `sprint-3`).
- **Gemeinsamer Fix:** Die Kontotypen überschreiben `withdraw(date, amount, text)`, sonst konnte
  man ihre Regel mit einem Buchungstext umgehen.
- **PR nach `main`:** #4 (`sprint-3` → `main`).

## Sprint 4 – Factory Pattern

Auftrag: `04-Exercise-factory-pattern.md`

- **Ledion Sakaj:** `AccountFactoryTests` (zuerst geschrieben, TDD), JavaDoc und README.
  PR #5 (`dev-sakaj` → `sprint-4`).
- **Louis Engel:** Enum `AccountType` mit den Präfixen `S`, `Y`, `P`; `create(AccountType, long)`
  mit `switch`; die Factory erstellt jetzt die richtigen Kontotypen; thread-sicherer Zähler mit
  `AtomicLong`; Klassendiagramm neu gezeichnet. PR #6 (`dev-engel` → `sprint-4`).
- **PR nach `main`:** #7 (`sprint-4` → `main`).

## Sprint 5 – Singleton Pattern

Auftrag: `05-Exercise-singleton-pattern.md`

- **Was:**
  - `Bank` ist ein Singleton: nicht öffentlicher Konstruktor, `getInstance()`, thread-sicher und
    lazy mit dem Holder-Idiom.
  - Die drei Varianten (eager, lazy mit `synchronized`, Holder) sind im JavaDoc von `Bank`
    verglichen, inklusive der Frage, warum Double-Checked Locking ohne `volatile` falsch ist.
  - Die `AccountFactory` wird über den Konstruktor in die `Bank` gegeben.
  - Vor- und Nachteile des Singletons stehen im Klassen-JavaDoc von `Bank`.
  - `BankTests` verwenden `Bank.getInstance()`; die Test-Isolation löst `Bank.reset()` in einem
    `@BeforeEach`. Das ist bewusst als Workaround (Code Smell) dokumentiert.
  - Neue Tests: Identität (`assertSame`), kein öffentlicher Konstruktor (Reflection),
    gleichzeitige Aufrufe aus 50 Threads, Isolation zwischen zwei Tests, Konstruktor mit
    eigener Factory (`BankConstructorTests`).
  - Klassendiagramm auf Sprint 5 nachgeführt.
- **Wer:** Silvan Castelberg.
- **PR:** #8 (`sprint-5` → `main`).
