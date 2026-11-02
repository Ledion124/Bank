package ch.bbw.accounts;


import ch.bbw.Scheduled;
import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;

/**
 * Lohnkonto.
 *
 * <p>
 * Eigene Regel: <b>Überziehen bis zur Kreditlimite</b>. Der Saldo darf durch
 * eine Abhebung negativ werden, aber nie unter {@link #getCreditLimit()} fallen.
 * Eine Abhebung, die das Limit unterschreiten würde, wird mit einer
 * {@link InvalidAmountException} abgelehnt und nicht gebucht.
 * </p>
 *
 * <p>
 * Die Kreditlimite ist eine negative Zahl oder 0 (tiefster erlaubter Saldo in
 * Millirappen). Mit Limit 0 verhält sich das Konto wie ein Sparkonto.
 * </p>
 *
 * <p>
 * Superklasse {@link Account} statt {@link SavingsAccount}: Ein Lohnkonto
 * <i>ist kein</i> Sparkonto, denn es erlaubt genau das Überziehen, das ein
 * Sparkonto verbietet. Würde es von {@code SavingsAccount} erben, müsste es
 * dessen Überzugsverbot wieder aushebeln. Validierung von Betrag und Datum
 * sowie das Buchen übernimmt {@link Account}; hier steht nur die Limite.
 * </p>
 *
 * @author Louis Engel
 * @version 1.0
 */
public class SalaryAccount extends Account
{
	/**
	 * Kreditlimite in Millirappen (negativ oder 0).
	 */
	private final long creditLimit;

	/**
	 * Initialisiert ein Lohnkonto.
	 *
	 * @param id          die Kontonummer
	 * @param creditLimit die Kreditlimite in Millirappen, negativ oder 0
	 *                    (z.B. -50_000_000 = CHF -500.00)
	 * @throws IllegalArgumentException falls die Kreditlimite positiv ist
	 */
	public SalaryAccount(String id, long creditLimit)
	{
		super(id);

		if (creditLimit > 0)
			throw new IllegalArgumentException("creditLimit must not be positive: " + creditLimit);

		this.creditLimit = creditLimit;
	}

	/**
	 * Gibt die Kreditlimite zurück.
	 *
	 * @return die Kreditlimite in Millirappen (negativ oder 0)
	 */
	public long getCreditLimit()
	{
		return creditLimit;
	}

	/**
	 * Hebt den gegebenen Betrag vom Konto ab, sofern der Saldo danach
	 * nicht unter die Kreditlimite fällt.
	 *
	 * <p>
	 * Grenzfall: Abheben bis <i>genau</i> zur Kreditlimite ist erlaubt.
	 * </p>
	 *
	 * <p>
	 * Überschrieben wird die Variante mit Buchungstext, weil sie die eigentliche
	 * Abhebung ausführt. {@link Account#withdraw(Scheduled, long)} ruft sie mit dem
	 * Standardtext auf, so gilt die Kreditlimite für beide Varianten.
	 * </p>
	 *
	 * @param date   das Transaktionsdatum
	 * @param amount der abzuhebende Betrag in Millirappen
	 * @param text   der Buchungstext
	 * @return der neue Saldo
	 * @throws InvalidAmountException falls die Kreditlimite unterschritten würde
	 *                                oder der Betrag nicht grösser als 0 ist
	 * @throws InvalidDateException   falls das Datum in der Vergangenheit liegt
	 */
	@Override
	public long withdraw(Scheduled date, long amount, String text) throws InvalidAmountException, InvalidDateException
	{
		if (getBalance() - amount < creditLimit)
			throw new InvalidAmountException("Withdrawal would exceed credit limit of " + creditLimit);

		return super.withdraw(date, amount, text);
	}
}
