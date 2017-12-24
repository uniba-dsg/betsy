package betsy.common.timeouts.timeout;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import betsy.common.timeouts.calibration.CalibrationTimeout;

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
    public static Timeout getTimeout(String key) {
        return TIMEOUTS.getTimeout(Objects.requireNonNull(key, "The key can't be null."));
    }

    /**
     * This method sets the values of the {@link Timeout} to {@link Timeout} with the corresponding link
     * in  the {@link TimeoutRepository}, if the {@link Timeout} exists in the {@link Timeouts}.
     *
     * @param timeout The {@link Timeout}, which should be set.
     */
    public static void setTimeout(Timeout timeout) {
        TIMEOUTS.setTimeout(Objects.requireNonNull(timeout, "The timeout can't be null."));
    }


    /**
     * This method sets the values of the {@link Timeout} to the {@link TimeoutRepository}, if the {@link Timeout} exists in the {@link Timeouts}.
     *
     * @param calibrationTimeouts A {@link HashMap} with the key and the {@link Timeout}.
     */
    public static void setAllCalibrationTimeouts(HashMap<String, CalibrationTimeout> calibrationTimeouts) {
        Objects.requireNonNull(calibrationTimeouts, "The calibrationTimeouts can't be null.").values().forEach(TimeoutRepository::setTimeout);
    }

    /**
     *
     * The method returns all calibrateable timeouts.
     *
     * @return Returns all timeouts, which are calibrateable as {@link HashMap}}.
     */
    public static HashMap<String, Timeout> getAllCalibrateable() {
        HashMap<String, Timeout> timeouts = new HashMap<>();
        TIMEOUTS.getAllTimeouts().forEach((e, k) -> {
            if(k.getCategory() == Timeout.Category.MEASURABLE){
                timeouts.put(e, k);
            }
        });
        return timeouts;
    }
}

