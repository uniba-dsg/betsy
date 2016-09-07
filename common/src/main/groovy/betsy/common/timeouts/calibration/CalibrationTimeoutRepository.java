package betsy.common.timeouts.calibration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

import betsy.common.timeouts.timeout.Timeouts;

/**
 * @author Christoph broeker
 * @version 1.0
 */
public class CalibrationTimeoutRepository {

    public static final CalibrationTimeouts CALIBRATION_TIMEOUTS = new CalibrationTimeouts();

    /**
     * This method returns all timeouts as {@link CalibrationTimeout}.
     *
     * @return A {@link HashMap} with the key of {@link CalibrationTimeout} and the {@link CalibrationTimeout}.
     */
    public static HashMap<String, CalibrationTimeout> getAllCalibrationTimeouts() {
        return CALIBRATION_TIMEOUTS.getAllCalibrationTimeouts();
    }

    /**
     * With this method it is possible to add a {@link CalibrationTimeout} to the repository.
     *
     * @param calibrationTimeout The calibrationTime to add to the repository.
     */
    public static void addCalibrationTimeout(CalibrationTimeout calibrationTimeout) {
        CALIBRATION_TIMEOUTS.addCalibrationTimeout(Objects.requireNonNull(calibrationTimeout, "The calibrationTimeout can't be null."));
    }


    /**
     * This method writes all given {@link CalibrationTimeout} to the properties, if the {@link CalibrationTimeout} exists in the {@link Timeouts}.
     */
    public static void writeAllCalibrationTimeoutsToProperties() {
        CALIBRATION_TIMEOUTS.writeAllCalibrationTimeoutsToProperties();
    }

    /**
     * Writes the the values of the {@link CalibrationTimeout} in the {@link CalibrationTimeoutRepository} to a csv file.
     *
     * @param csv The csv path, where the timeout values should be saved.
     * @param numberOfDuration The number of calibration iterations.
     */
    public static void writeToCSV(Path csv, int numberOfDuration) {
        CALIBRATION_TIMEOUTS.writeToCSV(Objects.requireNonNull(csv, "The csv can't be null."), Objects.requireNonNull(numberOfDuration, "The numberOfDuration can't be null."));
    }

    /**
     * Writes the the values of the {@link CalibrationTimeout} in the {@link CalibrationTimeoutRepository} to a csv file.
     */
    public static void writeToCSV() {
        CALIBRATION_TIMEOUTS.writeToCSV();
    }

    /**
     *  This method removes all values from the repository.
     */
    public static void clean(){
        CALIBRATION_TIMEOUTS.clean();
    }

    /**
     * This method returns all CalibrationTimeouts once only.
     *
     * @return A {@link HashMap} with the timeouts.
     */
    public static HashMap<String, CalibrationTimeout> getAllNonRedundantTimeouts() {
        return CALIBRATION_TIMEOUTS.getAllNonRedundantTimeouts();
    }
}

