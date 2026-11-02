package ch.bbw.exceptions;

/**
 * Wird geworfen, wenn ein Betrag ungültig ist.
 *
 * <p>
 * Beispiele: ein Betrag kleiner oder gleich 0 bei einer Ein- oder Auszahlung,
 * oder eine Abhebung, die ein Sparkonto überziehen würde.
 * </p>
 */
public class InvalidAmountException extends Exception {
    /**
     * Erzeugt die Exception mit einer Fehlermeldung.
     *
     * @param message beschreibt, warum der Betrag ungültig ist
     */
    public InvalidAmountException(String message) {
        super(message);
    }
}
