package betsy.virtual.host.virtualbox.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import org.apache.log4j.Logger;

/**
 * Use the {@link InputStreamLogger} to capture a given {@link InputStream} and
 * log all it's content to a log4j {@link Logger}.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class InputStreamLogger extends Thread {

	private final InputStream stream;
	private final String description;

	private final static Logger log = Logger.getLogger(InputStreamLogger.class);

	public InputStreamLogger(final InputStream stream, final String desc) {
		this.stream = Objects.requireNonNull(stream);
		this.description = Objects.requireNonNull(desc);
	}

	@Override
	public void run() {
		try (InputStreamReader inpStrd = new InputStreamReader(stream);
				BufferedReader buffRd = new BufferedReader(inpStrd)) {
			String line;
			while ((line = buffRd.readLine()) != null) {
				log.info(description + ": " + line);
			}
		} catch (Exception exception) {
			log.fatal("Exception while logging stream output:", exception);
		}
	}

}