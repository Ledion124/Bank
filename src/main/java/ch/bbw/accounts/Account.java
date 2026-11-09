package ch.bbw.accounts;

import ch.bbw.Booking;
import ch.bbw.Scheduled;
import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Konto: gemeinsame Basis aller Kontotypen.
 *
 * <p>
 * Ein Konto hat eine Kontonummer und eine Liste von Buchungen. Es nimmt Ein- und
 * Auszahlungen an, prüft Betrag (&gt; 0) und Datum (nicht in der Vergangenheit) und
 * speichert jede Transaktion als {@link Booking}. Der Saldo wird nicht separat
 * gespeichert, sondern immer aus der Summe der Buchungen berechnet (eine einzige
 * Quelle der Wahrheit). Die Unterklassen ergänzen nur ihre eigene Abhebungsregel.
 * </p>
 *
 * @author Luigi Cavuoti, lro@gmx.ch, Ledion Sakaj, Silvan Castelberg
 * @version 3.0
 */
public abstract class Account {
    /**
     * Die Kontonummer (kann auch Buchstaben und Sonderzeichen enthalten).
     */
    private final String id;
    /**
     * Die Buchungen.
     */
    private final List<Booking> bookings;
    /**
     * Erzeugt ein neues Konto.
     *
     * @param id die Kontonummer
     */
    public Account(String id) {
        this.id = id;
        this.bookings = new ArrayList<Booking>();
    }

    /**
     * Gibt die Kontonummer zurück.
     *
     * @return die Kontonummer
     */
    public String getId() {
        return id;
    }

    /**
     * Gibt die Bezeichnung des Kontotyps zurück, z.B. "Sparkonto".
     * Jede Unterklasse muss diese Methode selbst beantworten.
     *
     * @return der Kontotyp
     */
    public abstract String getAccountType();

    /**
     * Gibt den Saldo zurück. Er wird aus der Summe aller Buchungsbeträge berechnet.
     *
     * @return der Saldo in Millirappen; 0 bei einem Konto ohne Buchungen
     */
    public long getBalance() {
        return bookings.stream()
                .mapToLong(Booking::amount)
                .sum();
    }

    /**
     * Gibt alle Buchungen des Kontos zurück.
     *
     * @return eine unveränderbare Kopie der Buchungsliste
     */
    public List<Booking> getBookings() {
        return List.copyOf(bookings);
    }

    /**
     * Checks if the transaction date is invalid, i.e. in the past.
     *
     * @param date the transaction date
     * @return true if the date is in the past (invalid), false if it is now or in the future
     */
    public boolean isInvalidTransaction(Scheduled date) {
        return date.isPastDue();
    }

    /**
     * Deposits the given amount with the default text "Einzahlung".
     *
     * @param date   the transaction date
     * @param amount the amount to deposit
     * @return the new balance of the account
     * @throws InvalidAmountException if the amount is not greater than 0
     * @throws InvalidDateException   if the date is in the past
     */
    public long deposit(Scheduled date, long amount) throws InvalidAmountException, InvalidDateException {
        return deposit(date, amount, "Einzahlung");
    }

    /**
     * Deposits the given amount to the account.
     *
     * @param date   the transaction date
     * @param amount the amount to deposit
     * @param text   the booking text; null or blank becomes "Buchung"
     * @return the new balance of the account
     * @throws InvalidAmountException if the amount is not greater than 0 (no booking is added)
     * @throws InvalidDateException   if the date is in the past (no booking is added)
     */
    public long deposit(Scheduled date, long amount, String text) throws InvalidAmountException, InvalidDateException {
        if (amount <= 0)
            throw new InvalidAmountException("amount must be greater than 0");

        if (isInvalidTransaction(date))
            throw new InvalidDateException("date mustn't be in the past");

        bookings.add(new Booking(date.toUnix(), amount, text));

        return getBalance();
    }

    /**
     * Withdraws the given amount with the default text "Auszahlung".
     *
     * @param date   the transaction date
     * @param amount the amount to withdraw
     * @return the new balance of the account
     * @throws InvalidAmountException if the amount is not greater than 0 or the account type's rule forbids it
     * @throws InvalidDateException   if the date is in the past
     */
    public long withdraw(Scheduled date, long amount) throws InvalidAmountException, InvalidDateException {
        return withdraw(date, amount, "Auszahlung");
    }

    /**
     * Hebt den gegebenen Betrag vom Konto ab.
     *
     * @param date   das Transaktionsdatum
     * @param amount der abzuhebende Betrag
     * @param text   der Buchungstext; null oder leer wird zu "Buchung"
     * @return der neue Saldo des Kontos
     * @throws InvalidAmountException falls der Betrag nicht grösser als 0 ist (es wird nichts gebucht)
     * @throws InvalidDateException   falls das Datum in der Vergangenheit liegt (es wird nichts gebucht)
     */
    public long withdraw(Scheduled date, long amount, String text) throws InvalidAmountException, InvalidDateException {
        if (amount <= 0)
            throw new InvalidAmountException("amount must be greater than 0");

        if (isInvalidTransaction(date))
            throw new InvalidDateException("date mustn't be in the past");

        // Achtung: hier Buchung mit negativem Betrag!
        bookings.add(new Booking(date.toUnix(), -amount, text));

        return getBalance();
    }

    /**
     * Erstellt einen Kontoauszug: eine Zeile pro Buchung
     * (Datum, Betrag, Text) und am Ende den Saldo.
     *
     * @return der Kontoauszug als Text
     */
    public String printStatement() {
        StringBuilder sb = new StringBuilder();

        for (Booking booking : bookings) {
            sb.append(Instant.ofEpochMilli(booking.date()));
            sb.append("  ");
            sb.append(formatAmount(booking.amount()));
            sb.append("  ");
            sb.append(booking.text());
            sb.append("\n");
        }

        sb.append("Saldo: ");
        sb.append(formatAmount(getBalance()));

        return sb.toString();
    }

    /**
     * Erstellt einen Kontoauszug für einen Monat: eine Zeile pro Buchung
     * aus diesem Monat (Datum, Betrag, Text) und am Ende den Saldo.
     *
     * @param year  das Jahr, z.B. 2026
     * @param month der Monat, 1 = Januar … 12 = Dezember
     * @return der Kontoauszug des Monats als Text
     */
    public String printStatement(int year, int month) {
        StringBuilder sb = new StringBuilder();

        for (Booking booking : bookings) {
            LocalDate day = Instant.ofEpochMilli(booking.date()).atZone(ZoneId.systemDefault()).toLocalDate();

            if (day.getYear() == year && day.getMonthValue() == month) {
                sb.append(Instant.ofEpochMilli(booking.date()));
                sb.append("  ");
                sb.append(formatAmount(booking.amount()));
                sb.append("  ");
                sb.append(booking.text());
                sb.append("\n");
            }
        }

        sb.append("Saldo: ");
        sb.append(formatAmount(getBalance()));

        return sb.toString();
    }

    /**
     * Wandelt einen Betrag in Millirappen in einen lesbaren CHF-Text um.
     *
     * @param millirappen der Betrag in Millirappen
     * @return der Betrag als Text, z.B. "CHF 1.23456"
     */
    private String formatAmount(long millirappen) {
        double chf = millirappen / 100_000.0;
        String wert = String.format("CHF %.5f", chf);
        return wert;
    }
}
