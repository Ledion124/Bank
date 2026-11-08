# Bank – Agiles Projekt (m320)

Eine kleine Bank-Software in Java, entwickelt in Sprints nach dem Auftrag des Moduls
m320 *Objektorientiertes Design* an der BBW. Die Bank verwaltet Konten verschiedener Typen,
auf die man Geld einzahlen und von denen man Geld abheben kann. Jede Ein- und Auszahlung wird
als **Buchung** gespeichert, und pro Konto lässt sich ein lesbarer **Kontoauszug** erstellen.

Wir arbeiten **test-first (TDD)**: Zu jeder Änderung werden zuerst die JUnit-Tests geschrieben
und committet, danach der Code, der sie grün macht.

## Team

| Person | Schwerpunkt |
|---|---|
| Ledion Sakaj (Ledion124) | Sprint 2 (Buchungen), Tests für Sprint 1 (`BankTests`) und Sprint 4 (`AccountFactoryTests`), README, JavaDoc |
| Louis Engel (Louiss123) | Sprint 3 (`SalaryAccount`), Sprint 4 (Factory), Klassendiagramm |
| Silvan Castelberg (Silvanca) | Sprint 3 (`PromoYouthSavingsAccount`, `getAccountType`), Sprint 5 (Singleton), Use Cases |

## Sprint-Übersicht

| Sprint | Thema | Auftrag | Stand |
|---|---|---|---|
| 1 | Baseline: `Bank` + `Account` (einzahlen, abheben, Saldo) | Vorlage | ✅ Code aus der Vorlage, Tests von uns |
| 2 | Buchungen mit Text, Saldo aus Buchungen, Kontoauszug | `02-Exercise-bookings.md` | ✅ PR #1 |
| 3 | Kontotypen / Vererbung | `03-Exercise-specific-accounts.md` | ✅ PR #2, #3, #4 |
| 4 | Factory Pattern | `04-Exercise-factory-pattern.md` | in Arbeit (Tests geschrieben) |
| 5 | Singleton Pattern | `05-Exercise-singleton-pattern.md` | offen |

Die Aufträge liegen in [`new-bank/docs/exercises/`](new-bank/docs/exercises/).

## Arbeitsweise

- **TDD:** Pro Feature zuerst ein `test(sprintN): …`-Commit (rote Tests), danach `feat(sprintN): …`.
- **Branches:** Jede Person arbeitet auf einem eigenen Branch (`dev-…`). Fertige Arbeit kommt per
  Pull Request in den Sprint-Branch (`sprint-2`, `sprint-3`, …) und von dort per Pull Request in `main`.
  Jeder Pull Request wird von einer anderen Person reviewt.
- **Commit-Nachrichten:** `typ(sprintN): beschreibung`, z. B. `feat(sprint2): add text to booking`.

## Aufbau

```
Bank_Auftrag/
├── README.md                    dieses Dokument
├── docs/
│   ├── Klassendiagramm.md
│   └── use-cases/UC-*.md        Use-Case-Beschreibungen
└── new-bank/                    Maven-Projekt (Java 21, Spring Boot)
    ├── src/main/java/ch/bbw/
    │   ├── Bank.java            verwaltet alle Konten
    │   ├── AccountFactory.java  erstellt Konten und vergibt Kontonummern
    │   ├── Booking.java         eine Buchung (date, amount, text)
    │   ├── Scheduled.java       Transaktionsdatum
    │   ├── accounts/            Account (abstrakt) und die Kontotypen
    │   └── exceptions/          InvalidAmountException, InvalidDateException
    └── src/test/java/           JUnit-5-Tests
```

## Architektur

Jede Klasse hat genau eine Aufgabe:

| Klasse | Verantwortung |
|---|---|
| `Bank` | verwaltet die Konten (nach Kontonummer) und leitet Ein-/Auszahlungen weiter; kennt nur den abstrakten Typ `Account` |
| `AccountFactory` | erstellt Konten des richtigen Typs und vergibt die Kontonummern |
| `Account` (abstrakt) | hält die Buchungen, prüft Betrag und Datum, berechnet den Saldo und erstellt den Kontoauszug |
| `SavingsAccount`, `SalaryAccount`, `PromoYouthSavingsAccount` | ergänzen nur ihre eigene Abhebungsregel |
| `Booking` | eine unveränderliche Buchung (Datum, Betrag, Text) |
| `Scheduled` | ein Transaktionsdatum relativ zu jetzt |

Vererbung: `Account` → `SavingsAccount` → `PromoYouthSavingsAccount`, und `Account` → `SalaryAccount`.
`Account` definiert die abstrakte Methode `getAccountType()`, die jeder Kontotyp beantwortet.
Die Kontotypen überschreiben `withdraw(date, amount, text)`, damit ihre Regel auch für
Abhebungen mit Buchungstext gilt.

## Starten und testen

Voraussetzung: **JDK 21**. Maven muss nicht installiert sein, das Projekt bringt den
Maven-Wrapper mit. Alle Befehle im Ordner `new-bank` ausführen (unter Windows `mvnw.cmd`
statt `./mvnw`):

```bash
./mvnw test              # alle Tests laufen lassen
./mvnw spring-boot:run   # Anwendung starten (http://localhost:8080)
```

## Domäne

- **Millirappen:** Alle Beträge werden als ganze Zahl (`long`) in Millirappen gespeichert,
  1 CHF = 100 000 Millirappen. So entstehen keine Rundungsfehler wie bei Kommazahlen.
  Für den Kontoauszug werden Beträge in CHF mit 5 Nachkommastellen angezeigt.
- **Buchungen:** Jede Einzahlung und Auszahlung erzeugt eine `Booking` mit Datum
  (Unix-Zeitstempel in Millisekunden), Betrag und Text. Auszahlungen haben einen negativen
  Betrag. Der **Saldo** eines Kontos wird immer aus der Summe seiner Buchungen berechnet.
  Ist der Text leer, wird `"Buchung"` verwendet.
- **Validierung:** Beträge müssen grösser als 0 sein, und das Datum darf nicht in der
  Vergangenheit liegen. Abgelehnte Buchungen verändern das Konto nicht.
- **Kontotypen:**
  - `SavingsAccount` (Sparkonto): darf nicht überzogen werden.
  - `SalaryAccount` (Lohnkonto): darf bis zur Kreditlimite überzogen werden (negative Zahl oder 0;
    eine positive Limite wird abgelehnt).
  - `PromoYouthSavingsAccount` (Promo-Jugendsparkonto): ein Sparkonto, auf dem immer mindestens
    10 CHF bleiben müssen (Mindestsaldo).

  Kontonummern haben ein Präfix je Typ: `S-` (Sparkonto), `P-` (Lohnkonto), `Y-` (Jugendsparkonto).

## Weitere Dokumentation

- Use Cases: [`docs/use-cases/`](docs/use-cases/)
- Klassendiagramm: [`docs/Klassendiagramm.md`](docs/Klassendiagramm.md)
- Sprint-Dokumentation (was, wer, welcher PR): [`docs/Sprints.md`](docs/Sprints.md)
- Ursprüngliches README der Vorlage (Lehrer): [`new-bank/README-Vorlage.md`](new-bank/README-Vorlage.md)
