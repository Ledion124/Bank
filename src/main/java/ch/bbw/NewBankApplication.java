package ch.bbw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Einstiegspunkt der Spring-Boot-Anwendung.
 *
 * <p>
 * Startet den eingebetteten Webserver auf <code>localhost:8080</code>:
 * </p>
 * <pre>./mvnw spring-boot:run</pre>
 *
 * <p>
 * Die Annotation {@code @SpringBootApplication} aktiviert das Component-Scanning
 * ab diesem Package ({@code ch.bbw}) sowie die Autokonfiguration. Die
 * Domänenklassen ({@link Bank}, {@link AccountFactory}, …) sind bewusst
 * <em>keine</em> Spring-Beans — sie bleiben reines OO-Design. Der Service-Layer,
 * der sie zu Beans macht, entsteht in Sprint 6 (siehe
 * {@code docs/exercises/10-Exercise-build-the-be.md}).
 * </p>
 *
 * @author Luigi Cavuoti, Ledion Sakaj
 * @version 1.0
 */
@SpringBootApplication
public class NewBankApplication {

	/**
	 * Startet die Spring-Boot-Anwendung.
	 *
	 * @param args Kommandozeilen-Argumente, werden an Spring weitergegeben
	 */
	public static void main(String[] args) {
		SpringApplication.run(NewBankApplication.class, args);
	}
}
