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
 * Konto.
 *
 * @author Luigi Cavuoti, lro@gmx.ch
 * @version 2.1
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
     * Gibt das Saldo zur�ck.
     *
     * @return Saldo
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
     * Checks if the transaction date is not in the past.
     *
     * @param date the transaction date
     * @return whether the transaction date is valid
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
     */
    public long deposit(Scheduled date, long amount) throws InvalidAmountException, InvalidDateException {
        return deposit(date, amount, "Einzahlung");
    }

    /**
     * Deposits the given amount to the account.
     *
     * @param date   the transaction date
     * @param amount the amount to deposit
     * @param text   the booking text
     * @return the new balance of the account
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
     */
    public long withdraw(Scheduled date, long amount) throws InvalidAmountException, InvalidDateException {
        return withdraw(date, amount, "Auszahlung");
    }

    /**
     * Hebt den gegebenen Betrag vom Konto ab.
     *
     * @param date   das Transaktionsdatum
     * @param amount der abzuhebende Betrag
     * @param text   der Buchungstext
     * @return der neue Saldo des Kontos
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
