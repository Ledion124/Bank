package ch.bbw.accounts;


import ch.bbw.Scheduled;
import ch.bbw.exceptions.InvalidAmountException;
import ch.bbw.exceptions.InvalidDateException;

/**
 * Sparkonto.
 *
 * <p>
 * Eigene Regel: <b>kein Überziehen</b>. Eine Abhebung, die grösser als der
 * aktuelle Saldo ist, wird mit einer
 * {@link ch.bbw.exceptions.InvalidAmountException} abgelehnt.
 * </p>
 *
 * @author Luigi Cavuoti, lro@gmx.ch, Ledion Sakaj, Silvan Castelberg
 * @version 1.2
 */
public class SavingsAccount extends Account
{
	/**
	 * Initialisiert ein Sparkonto
	 *
	 * @param id die Kontonummer
	 */
	public SavingsAccount(String id)
	{
		super(id);
	}

	/**
	 * Gibt den Kontotyp zurück.
	 *
	 * @return "Sparkonto"
	 */
	@Override
	public String getAccountType()
	{
		return "Sparkonto";
	}

	/**
	 * Hebt den gegebenen Betrag vom Konto ab.
	 *
	 * @param date das Transaktionsdatum
	 * @param amount der abzuhebende Betrag
	 * @param text der Buchungstext
	 *
	 * @return der neue Saldo des Kontos
	 * @throws InvalidAmountException wenn der Betrag grösser als der Saldo ist (kein Überziehen)
	 */
	@Override
	public long withdraw(Scheduled date, long amount, String text) throws InvalidAmountException, InvalidDateException {
		if (getBalance() < amount)
			throw new InvalidAmountException("Amount is greater than balance");

		return super.withdraw(date, amount, text);
	}
}
