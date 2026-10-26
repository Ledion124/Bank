package ch.bbw;

/**
 * Buchung.
 *
 * @param date   Datum der Transaktion (Banktage seit 1.1.1970).
 * @param amount Transaktionsbetrag (Millirappen).
 * @author Luigi Cavuoti, lro@gmx.ch
 * @version 2.1
 */
public record Booking(long date, long amount) {
	/**
	 * Erzeugt eine neue Buchung
	 *
	 * @param date   long
	 *               Datum der Transaktion (Banktage seit 1.1.1970)
	 * @param amount long
	 *               Transaktionsbetrag (Millirappen)
	 */
	public Booking {
	}

	/**
	 * Gibt das Datum der Buchung zur�ck.
	 *
	 * @return int Datum (Banktage seit 1.1.1970)
	 */
	@Override
	public long date() {
		return date;
	}

	/**
	 * Gibt den Betrag zur�ck.
	 *
	 * @return long Betrag (in Millirappen)
	 */
	@Override
	public long amount() {
		return amount;
	}
}
