package ch.bbw.accounts;


import ch.bbw.Scheduled;
import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;

/**
 * Promo-Jugendsparkonto.
 *
 * <p>
 * Eigene Regel: <b>Mindestsaldo</b>. Auf dem Konto müssen nach jeder
 * Abhebung mindestens {@link #MIN_BALANCE} Millirappen (10 CHF) bleiben.
 * Eine Abhebung, die den Saldo unter diesen Betrag senken würde, wird mit
 * einer {@link InvalidAmountException} abgelehnt und nicht gebucht.
 * </p>
 *
 * <p>
 * Superklasse {@link SavingsAccount} statt {@link Account}: Ein
 * Promo-Jugendsparkonto <i>ist</i> ein Sparkonto (is-a) und erbt damit
 * das Überzugsverbot, ohne es neu zu implementieren. Der Preis dafür ist
 * die Kopplung an die Implementierung von {@code SavingsAccount}: Ändert
 * sich dort {@code withdraw(...)}, ändert sich auch dieses Konto.
 * </p>
 *
 * @author Silvan Castelberg
 * @version 1.0
 */
public class PromoYouthSavingsAccount extends SavingsAccount
{
	/**
	 * Mindestsaldo in Millirappen (10 CHF).
	 */
	public static final long MIN_BALANCE = 1_000_000L;

	/**
	 * Initialisiert ein Promo-Jugendsparkonto
	 *
	 * @param id die Kontonummer
	 */
	public PromoYouthSavingsAccount(String id)
	{
		super(id);
	}

	/**
	 * Gibt den Kontotyp zurück. Muss überschrieben werden, sonst käme
	 * "Sparkonto" aus {@link SavingsAccount}.
	 *
	 * @return "Promo-Jugendsparkonto"
	 */
	@Override
	public String getAccountType()
	{
		return "Promo-Jugendsparkonto";
	}

	/**
	 * Hebt den gegebenen Betrag vom Konto ab, sofern danach noch
	 * mindestens {@link #MIN_BALANCE} auf dem Konto bleibt.
	 *
	 * <p>
	 * Überschrieben wird die Variante mit Buchungstext, weil sie die eigentliche
	 * Abhebung ausführt. {@link Account#withdraw(Scheduled, long)} ruft sie mit dem
	 * Standardtext auf, so gilt der Mindestsaldo für beide Varianten.
	 * </p>
	 *
	 * @param date das Transaktionsdatum
	 * @param amount der abzuhebende Betrag
	 * @param text der Buchungstext
	 *
	 * @return der neue Saldo
	 * @throws InvalidAmountException falls der Mindestsaldo unterschritten würde
	 * oder der Betrag ungültig ist
	 * @throws InvalidDateException falls das Datum in der Vergangenheit liegt
	 */
	@Override
	public long withdraw(Scheduled date, long amount, String text) throws InvalidAmountException, InvalidDateException {
		if (getBalance() - amount < MIN_BALANCE)
			throw new InvalidAmountException("Balance must not fall below minimum balance of " + MIN_BALANCE);

		return super.withdraw(date, amount, text);
	}
}
