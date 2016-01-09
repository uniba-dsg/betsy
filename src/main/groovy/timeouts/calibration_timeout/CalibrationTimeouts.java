package timeouts.calibration_timeout;

import flex.messaging.io.ArrayCollection;
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
     * With this method it is possible to add a {@link CalibrationTimeout}.
     *
     * @param calibrationTimeout The {@link CalibrationTimeout} to add to the {@link CalibrationTimeouts}.
     */
    public void addCalibrationTimeout(CalibrationTimeout calibrationTimeout) {
        calibrationTimeouts.add(calibrationTimeout);
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
     * This method returns all {@link CalibrationTimeout}.
     *
     * @return A {@link HashMap} with the key of {@link CalibrationTimeout} and the {@link CalibrationTimeout}.
     */
    public HashMap<String, CalibrationTimeout> getAllCalibrationTimeouts() {
        HashMap<String, CalibrationTimeout> calibrationTimeoutsHashMap = new HashMap<>();
        for (CalibrationTimeout timeout : calibrationTimeouts) {
            calibrationTimeoutsHashMap.put(timeout.getCalibrationTimeoutKey(), timeout);
        }
        return calibrationTimeoutsHashMap;
    }

    /**
     * This method returns all CalibrationTimeouts once only.
     *
     * @return A {@link HashMap} with the timeouts.
     */
    public HashMap<String, CalibrationTimeout> getAllNonRedundantTimeouts() {
        HashMap<String, CalibrationTimeout> timeouts = getAllCalibrationTimeouts();
        HashMap<String, CalibrationTimeout> calibrationTimeouts = new HashMap<>();

        for (CalibrationTimeout calibrationTimeout : timeouts.values()) {
            if (!calibrationTimeouts.containsKey(calibrationTimeout.getKey())) {
                calibrationTimeouts.put(calibrationTimeout.getKey(), calibrationTimeout);
            } else {
                if (calibrationTimeout.getStatus() == CalibrationTimeout.Status.KEPT &&
                        calibrationTimeouts.get(calibrationTimeout.getKey()).getStatus() == CalibrationTimeout.Status.KEPT &&
                        calibrationTimeout.getTimeoutInMs() > calibrationTimeouts.get(calibrationTimeout.getKey()).getTimeoutInMs()) {
                    calibrationTimeouts.remove(calibrationTimeout.getKey());
                    calibrationTimeouts.put(calibrationTimeout.getKey(), calibrationTimeout);
                } else if (calibrationTimeout.getStatus() == CalibrationTimeout.Status.EXCEEDED) {
                    calibrationTimeouts.remove(calibrationTimeout.getKey());
                    calibrationTimeouts.put(calibrationTimeout.getKey(), calibrationTimeout);
                }

            }
        }
        return calibrationTimeouts;
    }

    /**
     * This method writes all given {@link CalibrationTimeout} to the properties.
     */
    public void writeAllCalibrationTimeoutsToProperties() {
        List<CalibrationTimeout> timeouts = new ArrayCollection(getAllNonRedundantTimeoutsProperties().values());
        TimeoutIOOperations.writeToProperties(properties, convertCalibrationTimeoutListToTimeoutList(timeouts));
    }

    /**
     * The method writes all timeouts to a csv file.
     *
     * @param csv The csv file, where the timeout values should be saved.
     * @param numberOfDuration The number of calibration iterations.
     */
    public void writeToCSV(File csv, int numberOfDuration) {
        TimeoutIOOperations.writeToCSV(csv, calibrationTimeouts, numberOfDuration);
    }

    /**
     * The method writes all timeouts to a csv file.
     */
    public void writeToCSV() {
        TimeoutIOOperations.writeToCSV(csv, calibrationTimeouts);
    }

    /**
     * This method removes all values from the timeout list.
     */
    public void clean() {
        calibrationTimeouts = new ArrayList<>();
    }

    private List<Timeout> convertCalibrationTimeoutListToTimeoutList(List<CalibrationTimeout> calibrationTimeouts) {
        return calibrationTimeouts.stream().collect(Collectors.toCollection(ArrayList::new));
    }

    private HashMap<String, CalibrationTimeout> getAllNonRedundantTimeoutsProperties() {
        HashMap<String, CalibrationTimeout> timeouts = getAllCalibrationTimeouts();
        HashMap<String, CalibrationTimeout> calibrationTimeouts = new HashMap<>();

        //write only kept and highest timeouts to the properties
        timeouts.values().stream().filter(calibrationTimeout -> calibrationTimeout.getStatus() == CalibrationTimeout.Status.KEPT).forEach(calibrationTimeout -> {
            if (!calibrationTimeouts.containsKey(calibrationTimeout.getKey())) {
                calibrationTimeouts.put(calibrationTimeout.getKey(), calibrationTimeout);
            } else if (calibrationTimeout.getTimeoutInMs() > calibrationTimeouts.get(calibrationTimeout.getKey()).getTimeoutInMs()) {
                calibrationTimeouts.remove(calibrationTimeout.getKey());
                calibrationTimeouts.put(calibrationTimeout.getKey(), calibrationTimeout);
            }
        });

        return calibrationTimeouts;
    }
}
