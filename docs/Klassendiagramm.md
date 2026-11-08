# Klassendiagramm

Stand: Sprint 5 (Singleton Pattern). Das Diagramm zeigt alle Java-Klassen aus
`new-bank/src/main/java/ch/bbw` mit Attributen, Methoden, Vererbung und Beziehungen.

Legende: `+` public, `-` private, `~` paketintern, `*` abstrakt, `$` statisch.
Beträge sind in Millirappen (`long`), Datumswerte in Millisekunden (Unix-Zeit).

```mermaid
classDiagram
    direction TB

    class NewBankApplication {
        +main(args: String[])$
    }

    class Bank {
        -accounts: TreeMap~String, Account~
        -accountFactory: AccountFactory
        ~Bank(accountFactory: AccountFactory)
        +getInstance() Bank$
        +reset()$
        +createSavingsAccount() String
        +createPromoYouthSavingsAccount() String
        +createSalaryAccount(creditLimit: long) String
        +getBalance() long
        +getBalance(id: String) long
        +deposit(id: String, date: Scheduled, amount: long)
        +deposit(id: String, date: Scheduled, amount: long, text: String)
        +withdraw(id: String, date: Scheduled, amount: long) long
        +withdraw(id: String, date: Scheduled, amount: long, text: String) long
        +top5HighestBalances() List~Account~
        +top5LowestBalances() List~Account~
        +getAccount(id: String) Account
    }

    class AccountFactory {
        -nextAccountId: AtomicLong
        +AccountFactory()
        +createSavingsAccount() Account
        +createPromoYouthSavingsAccount() Account
        +createSalaryAccount(creditLimit: long) Account
        +create(type: AccountType) Account
        +create(type: AccountType, creditLimit: long) Account
        -nextId(type: AccountType) String
    }

    class AccountType {
        <<enumeration>>
        SAVINGS
        PROMO_YOUTH
        SALARY
        -prefix: String
        +getPrefix() String
    }

    class Account {
        <<abstract>>
        -id: String
        -bookings: List~Booking~
        +Account(id: String)
        +getId() String
        +getAccountType()* String
        +getBalance() long
        +getBookings() List~Booking~
        +isInvalidTransaction(date: Scheduled) boolean
        +deposit(date: Scheduled, amount: long) long
        +deposit(date: Scheduled, amount: long, text: String) long
        +withdraw(date: Scheduled, amount: long) long
        +withdraw(date: Scheduled, amount: long, text: String) long
        +printStatement() String
        +printStatement(year: int, month: int) String
        -formatAmount(millirappen: long) String
    }

    class SavingsAccount {
        +SavingsAccount(id: String)
        +getAccountType() String
        +withdraw(date: Scheduled, amount: long, text: String) long
    }

    class PromoYouthSavingsAccount {
        +MIN_BALANCE: long = 1000000$
        +PromoYouthSavingsAccount(id: String)
        +getAccountType() String
        +withdraw(date: Scheduled, amount: long, text: String) long
    }

    class SalaryAccount {
        -creditLimit: long
        +SalaryAccount(id: String, creditLimit: long)
        +getCreditLimit() long
        +getAccountType() String
        +withdraw(date: Scheduled, amount: long, text: String) long
    }

    class Booking {
        <<record>>
        +date: long
        +amount: long
        +text: String
        +Booking(date: long, amount: long, text: String)
    }

    class Scheduled {
        -millisecondsUntilDue: long
        +Scheduled(millisecondsUntilDue: long)
        +now()$ Scheduled
        +isPastDue() boolean
        +toUnix() long
        +toInstant() Instant
        +plus(duration: long, unit: ChronoUnit) Scheduled
        +plusMillis(millisToAdd: long) Scheduled
        +plusSeconds(secondsToAdd: long) Scheduled
        +plusMinutes(minutesToAdd: long) Scheduled
        +plusHours(hoursToAdd: long) Scheduled
        +plusDays(daysToAdd: long) Scheduled
        +plusWeeks(weeksToAdd: long) Scheduled
        +plusMonths(monthsToAdd: long) Scheduled
        +plusYears(yearsToAdd: long) Scheduled
        +minus(duration: long, unit: ChronoUnit) Scheduled
        +minusMillis(millisToAdd: long) Scheduled
        +minusSeconds(secondsToAdd: long) Scheduled
        +minusMinutes(minutesToAdd: long) Scheduled
        +minusHours(hoursToAdd: long) Scheduled
        +minusDays(daysToAdd: long) Scheduled
        +minusWeeks(weeksToAdd: long) Scheduled
        +minusMonths(monthsToAdd: long) Scheduled
        +minusYears(yearsToAdd: long) Scheduled
        +toString() String
    }

    class Exception {
        <<Java>>
    }

    class InvalidAmountException {
        +InvalidAmountException(message: String)
    }

    class InvalidDateException {
        +InvalidDateException(message: String)
    }

    %% Vererbung
    Account <|-- SavingsAccount
    Account <|-- SalaryAccount
    SavingsAccount <|-- PromoYouthSavingsAccount
    Exception <|-- InvalidAmountException
    Exception <|-- InvalidDateException

    %% Beziehungen
    NewBankApplication ..> Bank : startet
    Bank "1" o-- "0..*" Account : verwaltet
    Bank "1" o-- "1" AccountFactory : bekommt im Konstruktor
    AccountFactory ..> Account : erstellt
    AccountFactory ..> AccountType : verwendet
    Account "1" *-- "0..*" Booking : hat
    Account ..> Scheduled : verwendet
    Account ..> InvalidAmountException : wirft
    Account ..> InvalidDateException : wirft
    Bank ..> InvalidAmountException : wirft
    Bank ..> InvalidDateException : wirft
```

## Erklärungen

- **Vererbung:** `Account` ist abstrakt und gibt mit `getAccountType()` eine
  abstrakte Methode vor. `SavingsAccount` und `SalaryAccount` erben direkt von
  `Account`, `PromoYouthSavingsAccount` erbt von `SavingsAccount`. Jede
  Unterklasse überschreibt `withdraw(date, amount, text)` mit ihrer eigenen
  Regel (kein negativer Saldo, Mindestsaldo bzw. Kreditlimite).
- **Factory Pattern:** `Bank` erstellt nie selbst ein Konto, sondern ruft die
  `AccountFactory` auf. Nur die Factory kennt die konkreten Kontoklassen, die
  `Bank` arbeitet nur mit der abstrakten Klasse `Account`.
- **Singleton Pattern:** Es gibt genau eine `Bank`, man holt sie mit
  `Bank.getInstance()` (Holder-Idiom, lazy und thread-sicher). Der Konstruktor
  ist paketintern und bekommt die `AccountFactory` hineingegeben, damit Tests
  eine eigene Bank bauen können. `reset()` leert die Bank und existiert nur für
  Tests (Workaround). Die Begründung steht im JavaDoc von `Bank`.
- **AccountType:** Das Enum hält Kontotyp und Präfix der Kontonummer
  (`S`, `Y`, `P`) an einem Ort zusammen.
- **Booking:** Ein Konto speichert seine Buchungen; der Saldo wird aus den
  Buchungen berechnet und nicht separat gespeichert.
