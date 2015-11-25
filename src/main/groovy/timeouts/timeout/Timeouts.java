package timeouts.timeout;

import org.apache.log4j.Logger;
import timeouts.TimeoutIOOperations;

import java.io.File;
import java.util.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Timeouts {

    private static final Logger LOGGER = Logger.getLogger(Timeouts.class);
    private List<Timeout> timeouts = new ArrayList<>();
    private File properties = new File("timeout.properties");
    private File csv = new File ("timeouts.csv");

    /**
     *
     * @param timeouts The timeouts, which should be managed by the {@link timeouts}.
     * @param nameOfProperties The name of the properties file.
     * @param nameOfCSV The name of the csv file.
     */
    public Timeouts(ArrayList<Timeout> timeouts, String nameOfProperties, String nameOfCSV) {
        if (nameOfProperties != null && nameOfProperties.length() > 0) {
            this.properties = new File(nameOfProperties);
        }
        if (nameOfCSV != null && nameOfCSV.length() > 0) {
            this.csv = new File(nameOfCSV + ".csv");
        }
        if(timeouts != null){
            this.timeouts = timeouts;
        }
    }

    /**
     * @param nameOfProperties The name of the properties file.
     * @param nameOfCSV The name of the csv file.
     */
    public Timeouts(String nameOfProperties, String nameOfCSV) {
        if (nameOfProperties != null && nameOfProperties.length() > 0) {
            this.properties = new File(nameOfProperties);
        }
        if (nameOfCSV != null && nameOfCSV.length() > 0) {
            this.csv = new File(nameOfCSV + ".csv");
        }
    }

    /**
     *
     * @param timeouts The timeouts, which should be managed by the {@link timeouts}.
     */
    public Timeouts(ArrayList<Timeout> timeouts) {
        if(timeouts != null){
            this.timeouts = timeouts;
        }
    }

    /**
     *
     */
    public Timeouts() {
    }

    /**
     * This method returns the {@link Timeout} for given key as {@link Optional}.
     *
     * @param key The key of the {@link Timeout}.
     * @return The {@link Timeout] for the given key as {@link Optional}.
     */
    public Optional<Timeout> getTimeout(String key) {
        return Optional.ofNullable(getAllTimeouts().get(key));
    }

    /**
     * This method returns a {@link HashMap} with all Timeouts as {@link HashMap}.
     *
     * @return A {@link HashMap} with the key as {@link String} and a {@link Timeout}.
     */
    public HashMap<String, Timeout> getAllTimeouts() {
        HashMap<String, Timeout> map = new HashMap<>();
        for (Timeout timeout : timeouts) map.put(timeout.getKey(), timeout);
        return map;
    }

    /**
     *
     * This method it is possible to change the values of an existing {@link Timeout}.
     *
     * @param timeout The {@link Timeout} with the new values.
     */
    public void setTimeout(Timeout timeout){
        if (getTimeout(timeout.getKey()).isPresent()) {
            getTimeout(timeout.getKey()).get().setValue(timeout.getTimeoutInMs());
            getTimeout(timeout.getKey()).get().setTimeToRepetition(timeout.getTimeToRepetitionInMs());
        } else {
            LOGGER.info("The timeout(" + timeout.getKey() + ") does not exist.");
        }
    }


    /**
     * This method writes all given {@link Timeout} to the properties.
     */
    public void writeAllTimeoutsToProperties() {
        TimeoutIOOperations.writeToProperties(properties, timeouts);
    }


    /**
     * This method reads all timeouts, which are set in the {@link Timeouts} from the {@link Properties}.
     */
    public void readTimeoutProperties() {
        timeouts = TimeoutIOOperations.readFromProperties(properties, timeouts);
    }

    /**
     *
     */
    public void writeToCSV() {
        TimeoutIOOperations.writeToCSV(csv, timeouts);
    }
}
