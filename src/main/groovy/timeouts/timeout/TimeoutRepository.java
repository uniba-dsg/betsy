package timeouts.timeout;

import timeouts.calibration_timeout.CalibrationTimeout;

import java.util.HashMap;
import java.util.Optional;

/**
 * This class handles the {@link Timeout} and provides methods to manage them.
 *
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutRepository {

    public static final Timeouts TIMEOUTS = new Timeouts();

    static {
        TIMEOUTS.readTimeoutProperties();
    }

    /**
     * This method returns the {@link Timeout} for given key as {@link Optional}.
     *
     * @param key The key of the {@link Timeout}.
     * @return The {@link Timeout] for the given key as {@link Optional}.
     */
    public static Optional<Timeout> getTimeout(String key) {
        return TIMEOUTS.getTimeout(key);
    }

    /**
     * This method sets the values of the {@link Timeout} to the {@link TimeoutRepository}, if the {@link Timeout} exists in the {@link Timeouts}.
     *
     * @param timeout The {@link Timeout}, which should be set.
     */
    public static void setTimeout(Timeout timeout) {
        TIMEOUTS.setTimeout(timeout);
    }


    /**
     * This method sets the values of the {@link Timeout} to the {@link TimeoutRepository}, if the {@link Timeout} exists in the {@link Timeouts}.
     *
     * @param calibrationTimeouts A {@link HashMap} with the key and the {@link Timeout}.
     */
    public static void setAllCalibrationTimeouts(HashMap<String, CalibrationTimeout> calibrationTimeouts) {
        calibrationTimeouts.values().forEach(TimeoutRepository::setTimeout);
    }

    /**
     * Writes the the values of the {@link Timeout} in the {@link TimeoutRepository} to a csv file.
     */
    public static void writeToCSV() {
        TIMEOUTS.writeToCSV();
    }

}
