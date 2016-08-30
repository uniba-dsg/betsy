package betsy.common.timeouts;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.common.timeouts.timeout.Timeout;
import org.apache.log4j.Logger;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Properties {

    private static final Logger LOGGER = Logger.getLogger(Properties.class);

    /**
     * This method reads the values of the given timeouts form the properties.
     *
     * @param propertiesFile The path of the properties.
     * @param timeouts       The list with the timeouts.
     * @return A {@link List} with the timeouts and the loaded values from the properties.
     */
    public static List<Timeout> read(Path propertiesFile, List<Timeout> timeouts) {
        Objects.requireNonNull(propertiesFile, "The propertiesFile can't be null.");
        Objects.requireNonNull(timeouts, "The timeouts can't be null.");
        if (Files.isReadable(propertiesFile)) {
            try (Reader reader = Files.newBufferedReader(propertiesFile)) {
                java.util.Properties properties = new java.util.Properties();
                properties.load(reader);

                for (Timeout timeout : timeouts) {
                    String value = properties.getProperty(timeout.getKey() + ".value");
                    if (value != null) {
                        try {
                            timeout.setValue(new Integer(value));
                        } catch (NumberFormatException e) {
                            LOGGER.info("The timeout with the key " + timeout.getKey() + " was not read from the properties. The timeout have to be an integer.");
                        }
                    }

                    String timeToRepetition = properties.getProperty(timeout.getKey() + ".timeToRepetition");
                    if (timeToRepetition != null) {
                        try {
                            timeout.setTimeToRepetition(new Integer(timeToRepetition));
                        } catch (NumberFormatException e) {
                            LOGGER.info("The timeToRepetition with the key " + timeout.getKey() + " was not read from the properties. The timeToRepetition have to be an integer.");
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.info("Couldn't read the {@link Timeout} properties.");
            }
        } else {
            LOGGER.info("The file " + propertiesFile.toString() + " is not readable.");
        }
        return timeouts;
    }

    /**
     * This method writes the values of the given timeouts to the property file.
     *
     * @param propertiesPath The path were the properties should be saved.
     * @param timeouts       The timeouts, which should be saved.
     */
    public static void write(Path propertiesPath, List<Timeout> timeouts) {
        Objects.requireNonNull(propertiesPath, "The propertiesFile can't be null.");
        Objects.requireNonNull(timeouts, "The timeouts can't be null.");
        java.util.Properties properties = new java.util.Properties();
        if (Files.isReadable(propertiesPath)) {
            try (Reader reader = Files.newBufferedReader(propertiesPath)) {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                LOGGER.info("Couldn't store the properties ");
            }
        } else {
            try (Writer writer = new FileWriter(propertiesPath.toString())) {
                for (Timeout timeout : timeouts) {
                    properties.setProperty(timeout.getKey() + ".value", Integer.toString(timeout.getTimeoutInMs()));
                    properties.setProperty(timeout.getKey() + ".timeToRepetition", Integer.toString(timeout.getTimeToRepetitionInMs()));
                }
                properties.store(writer, "Timeout_properties");
                writer.close();
            } catch (IOException e) {
                LOGGER.info("Couldn't store the properties ");
            }
        }

    }
}


