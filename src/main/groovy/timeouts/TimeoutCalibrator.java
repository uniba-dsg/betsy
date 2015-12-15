package timeouts;

import betsy.Main;
import betsy.bpel.BPELMain;
import betsy.bpel.soapui.SoapUIShutdownHelper;
import org.apache.log4j.Logger;
import timeouts.calibration_timeout.CalibrationTimeout;
import timeouts.calibration_timeout.CalibrationTimeoutRepository;
import timeouts.timeout.TimeoutRepository;

import java.util.HashMap;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutCalibrator {

    private static final Logger LOGGER = Logger.getLogger(TimeoutCalibrator.class);
    private static String actualTestFolder;

    public static void main(String[] args) {
        calibrateTimeouts(args);
    }

    private static void calibrateTimeouts(String[] args) {
        LOGGER.info("Calibration is started.");
        //If it's true, SoapUI turns off after first run
        BPELMain.shutdownSoapUiAfterCompletion(false);
        int counterTestFolder = 1;
        boolean allTimeoutsAreCalibrated = false;

        while (!allTimeoutsAreCalibrated) {

            Main.main(addChangedTestFolderToArgs(args, counterTestFolder++));

            //get used timeouts
            HashMap<String, CalibrationTimeout> timeouts = CalibrationTimeoutRepository.getAllCalibrationTimeouts();
            //calibrate the timeouts
            allTimeoutsAreCalibrated = handleTimeouts(timeouts);

            //sets all values to the repositories
            TimeoutRepository.setAllCalibrationTimeouts(timeouts);
            CalibrationTimeoutRepository.setAllCalibrationTimeouts(timeouts);

            for (CalibrationTimeout timeout : timeouts.values()) {
                LOGGER.info(timeout.getKey() + " " + timeout.getStatus() + " " + timeout.getTimeoutInMs());
            }
        }

        //write timeouts to properties and csv
        CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();
        CalibrationTimeoutRepository.writeToCSV();

        //shutdown SoapUI
        SoapUIShutdownHelper.shutdownSoapUIForReal();
        LOGGER.info("Calibration is finished.");
    }

    private static boolean handleTimeouts(HashMap<String, CalibrationTimeout> timeouts) {
        boolean allTimeoutsAreCalibrated = true;
        for (CalibrationTimeout timeout : timeouts.values()) {
            switch (timeout.getStatus()) {
                case NOT_SET:
                    timeout.setValue(timeout.getTimeoutInMs() - 100);
                    timeout.setStatus(CalibrationTimeout.Status.TOO_HIGH);
                    allTimeoutsAreCalibrated = false;
                    break;
                case TOO_HIGH:
                    if (timeout.getTimeoutInMs() - 100 < 0) {
                        timeout.setValue(0);
                        timeout.setStatus(CalibrationTimeout.Status.TOO_HIGH);
                    } else {
                        timeout.setValue(timeout.getTimeoutInMs() - 100);
                        timeout.setStatus(CalibrationTimeout.Status.CALIBRATED);
                    }
                    allTimeoutsAreCalibrated = false;
                    break;
                case TOO_LOW:
                    timeout.setValue(timeout.getTimeoutInMs() + 100);
                    timeout.setStatus(CalibrationTimeout.Status.CALIBRATED);
                    allTimeoutsAreCalibrated = false;
                    break;
                case CALIBRATED:
                    break;
            }
        }
        return allTimeoutsAreCalibrated;
    }

    private static String[] addChangedTestFolderToArgs(String args[], int i) {
        String[] destination = new String[args.length + 1];
        System.arraycopy(args, 0, destination, 0, 1);
        actualTestFolder = "test/test" + i;
        destination[1] = "-f" + actualTestFolder;
        System.arraycopy(args, 1, destination, 2, args.length - 1);
        return destination;
    }
}
