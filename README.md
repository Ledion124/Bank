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
| Ledion Sakaj (Ledion124) | Sprint 2 (Buchungen), `BankTests`, README |
| Louiss123 | Sprint 3 (`SalaryAccount`), Sprint 4 (Factory), Klassendiagramm |
| Silvanca | Sprint 3 (`PromoYouthSavingsAccount`), Sprint 5 (Singleton), Use Cases |

## Sprint-Übersicht

| Sprint | Thema | Auftrag | Stand |
|---|---|---|---|
| 1 | Baseline: `Bank` + `Account` (einzahlen, abheben, Saldo) | – | ✅ |
| 2 | Buchungen mit Text, Saldo aus Buchungen, Kontoauszug | `02-Exercise-bookings.md` | ✅ |
| 3 | Kontotypen / Vererbung | `03-Exercise-specific-accounts.md` | in Arbeit |
| 4 | Factory Pattern | `04-Exercise-factory-pattern.md` | offen |
| 5 | Singleton Pattern | `05-Exercise-singleton-pattern.md` | offen |

Die Aufträge liegen in [`new-bank/docs/exercises/`](new-bank/docs/exercises/).

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
- **Kontotypen:** `SavingsAccount` (Sparkonto, darf nicht überzogen werden),
  `SalaryAccount` (Lohnkonto, darf bis zu einer Kreditlimite überzogen werden) und
  `PromoYouthSavingsAccount` (Jugendsparkonto mit eigener Regel).
  Kontonummern haben ein Präfix je Typ: `S-`, `P-`, `Y-`.

## Weitere Dokumentation

- Use Cases: [`docs/use-cases/`](docs/use-cases/)
- Klassendiagramm: [`docs/Klassendiagramm.md`](docs/Klassendiagramm.md)
