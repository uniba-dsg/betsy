package timeouts.calibration_timeout;

import org.apache.log4j.Logger;
import timeouts.TimeoutIOOperations;
import timeouts.timeout.Timeout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class CalibrationTimeouts {

    private static final Logger LOGGER = Logger.getLogger(CalibrationTimeouts.class);
    private List<CalibrationTimeout> calibrationTimeouts = new ArrayList<>();
    private File properties = new File("timeout.properties");
    private File csv = new File("calibration_timeouts.csv");

    /**
     *
     */
    public CalibrationTimeouts() {
    }

    /**
     * @param calibrationTimeouts The calibrationTimeouts, which should be managed by the {@link CalibrationTimeouts}.
     * @param nameOfProperties    The name of the properties file.
     * @param nameOfCSV           The name of the csv file.
     */
    public CalibrationTimeouts(ArrayList<CalibrationTimeout> calibrationTimeouts, String nameOfProperties, String nameOfCSV) {
        if (nameOfProperties != null && nameOfProperties.length() > 0) {
            this.properties = new File(nameOfProperties);
        }
        if (nameOfCSV != null && nameOfCSV.length() > 0) {
            this.csv = new File(nameOfCSV + ".csv");
        }
        if (calibrationTimeouts != null) {
            this.calibrationTimeouts = calibrationTimeouts;
        }
    }

    /**
     * @param nameOfProperties The name of the properties file.
     * @param nameOfCSV        The name of the csv file.
     */
    public CalibrationTimeouts(String nameOfProperties, String nameOfCSV) {
        if (nameOfProperties != null && nameOfProperties.length() > 0) {
            this.properties = new File(nameOfProperties);
        }
        if (nameOfCSV != null && nameOfCSV.length() > 0) {
            this.csv = new File(nameOfCSV + ".csv");
        }
    }

    /**
     * @param calibrationTimeouts The calibrationTimeouts, which should be managed by the {@link CalibrationTimeouts}.
     */
    public CalibrationTimeouts(ArrayList<CalibrationTimeout> calibrationTimeouts) {
        if (calibrationTimeouts != null) {
            this.calibrationTimeouts = calibrationTimeouts;
        }
    }

    /**
     * With this method it is possible to add a {@link Timeout} to the {@link CalibrationTimeouts}.
     *
     * @param calibrationTimeout The {@link Timeout} to add to the {@link CalibrationTimeouts}.
     */
    public void addTimeout(CalibrationTimeout calibrationTimeout) {
        if (!getCalibrationTimeout(calibrationTimeout.getKey()).isPresent()) {
            calibrationTimeouts.add(calibrationTimeout);
        } else LOGGER.info("The timeout, which should be added to the CalibrationTimeouts, is already existing.");
    }

    /**
     * With this method it is possible to change the values of an existing {@link CalibrationTimeout} in the {@link CalibrationTimeouts}.
     *
     * @param calibrationTimeout The {@link CalibrationTimeout} with the new values.
     */
    public void setTimeoutCalibration(CalibrationTimeout calibrationTimeout) {
        if (getCalibrationTimeout(calibrationTimeout.getKey()).isPresent()) {
            getCalibrationTimeout(calibrationTimeout.getKey()).get().setValue(calibrationTimeout.getTimeoutInMs());
            getCalibrationTimeout(calibrationTimeout.getKey()).get().setStatus(calibrationTimeout.getStatus());
        } else {
            calibrationTimeouts.add(calibrationTimeout);
        }
    }

    /**
     * This method returns the {@link CalibrationTimeout} for given key as {@link Optional}.
     *
     * @param key The key of the {@link CalibrationTimeout}.
     * @return The {@link CalibrationTimeout] for the given key as {@link Optional}.
     */
    public Optional<CalibrationTimeout> getCalibrationTimeout(String key) {
        return Optional.ofNullable(getAllCalibrationTimeouts().get(key));
    }

    /**
     * This method returns all Timeouts as {@link CalibrationTimeout}.
     *
     * @return A {@link HashMap} with the key of {@link CalibrationTimeout} and the {@link CalibrationTimeout}.
     */
    public HashMap<String, CalibrationTimeout> getAllCalibrationTimeouts() {
        HashMap<String, CalibrationTimeout> calibrationTimeoutsHashMap = new HashMap<>();
        for (CalibrationTimeout timeout : calibrationTimeouts) {
            calibrationTimeoutsHashMap.put(timeout.getKey(), timeout);
        }
        return calibrationTimeoutsHashMap;
    }

    /**
     * This method writes all given {@link CalibrationTimeout} to the properties.
     */
    public void writeAllCalibrationTimeoutsToProperties() {
        TimeoutIOOperations.writeToProperties(properties, convertCalibrationTimeoutListToTimeoutList(calibrationTimeouts));
    }

    /**
     * The method writes all timeouts to a csv file.
     */
    public void writeToCSV() {
        TimeoutIOOperations.writeToCSV(csv, convertCalibrationTimeoutListToTimeoutList(calibrationTimeouts));
    }

    private List<Timeout> convertCalibrationTimeoutListToTimeoutList(List<CalibrationTimeout> calibrationTimeouts) {
        return calibrationTimeouts.stream().collect(Collectors.toCollection(ArrayList::new));
    }
}
