package ch.bbw.exceptions;

/**
 * Wird geworfen, wenn das Datum einer Transaktion ungültig ist,
 * zum Beispiel wenn es in der Vergangenheit liegt.
 *
 * @author Luigi Cavuoti, Ledion Sakaj
 * @version 1.1
 */
public class InvalidDateException extends Exception {
    /**
     * Erzeugt die Exception mit einer Fehlermeldung.
     *
     * @param message beschreibt, warum das Datum ungültig ist
     */
    public InvalidDateException(String message) {
        super(message);
    }
}
