package ch.bbw;

/**
 * Buchung: eine einzelne Ein- oder Auszahlung auf einem Konto.
 *
 * <p>
 * Eine Buchung ist unveränderlich (record). Einzahlungen haben einen positiven,
 * Auszahlungen einen negativen Betrag. Die Formatierung für den Kontoauszug
 * gehört nicht hierher, sondern zu {@link ch.bbw.accounts.Account}.
 * </p>
 *
 * @param date   Datum der Transaktion (Millisekunden seit 1.1.1970, Unix-Zeitstempel).
 * @param amount Transaktionsbetrag (Millirappen).
 * @param text   Buchungstext, z.B. "Lohn" oder "Miete".
 * @author Luigi Cavuoti, lro@gmx.ch, Ledion Sakaj
 * @version 3.0
 */
public record Booking(long date, long amount, String text) {
	/**
	 * Erzeugt eine neue Buchung
	 * Ist der Text null oder leer, wird "Buchung" verwendet.
	 *
	 * @param date   long
	 *               Datum der Transaktion (Millisekunden seit 1.1.1970, Unix-Zeitstempel)
	 * @param amount long
	 *               Transaktionsbetrag (Millirappen)
	 * @param text   String
	 *               Buchungstext, z. Bsp. "Lohn" oder "Miete"
	 */
	public Booking {
		if(text == null || text.isBlank()){
			text = "Buchung";
		}

	}
}
