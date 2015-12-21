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
        boolean lastRoundFinished = false;

        while (!lastRoundFinished) {
            lastRoundFinished = allTimeoutsAreCalibrated;

            Main.main(addChangedTestFolderToArgs(args, counterTestFolder++));
            //gather extern timeouts
            TimeoutIOOperations.readSoapUITimeouts(actualTestFolder);
            //get used timeouts
            HashMap<String, CalibrationTimeout> timeouts = CalibrationTimeoutRepository.getAllNonRedundantTimeouts();
            //calibrate the timeouts
            allTimeoutsAreCalibrated = handleTimeouts(timeouts);

            if (!allTimeoutsAreCalibrated || !lastRoundFinished) {
                //set all values to the repositories
                TimeoutRepository.setAllCalibrationTimeouts(timeouts);
                //clean the timeoutRepository for the new run
                CalibrationTimeoutRepository.clean();
                lastRoundFinished = false;
            }
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
                case EXCEEDED:
                    timeout.setValue(timeout.getTimeoutInMs() + 500);
                    allTimeoutsAreCalibrated = false;
                    break;
                case KEPT:
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
