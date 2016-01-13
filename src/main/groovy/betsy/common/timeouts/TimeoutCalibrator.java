package betsy.common.timeouts;

import betsy.Main;
import betsy.bpel.BPELMain;
import betsy.bpel.soapui.SoapUIShutdownHelper;
import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.calibration.CalibrationTimeout;
import betsy.common.timeouts.calibration.CalibrationTimeoutRepository;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

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
        int numberOfDuration = 0;
        boolean allTimeoutsAreCalibrated;
        boolean lastRound = false;
        boolean finished = false;

        File csv  = new File("calibration_timeouts.csv");
        FileTasks.deleteFile(csv.toPath());

        while (!finished) {
            //execute betsy
            Main.main(addChangedTestFolderToArgs(args, numberOfDuration++));
            //gather extern timeouts
            ExternalTimeouts.readSoapUITimeouts(actualTestFolder);
            //get used timeouts
            HashMap<String, CalibrationTimeout> timeouts = CalibrationTimeoutRepository.getAllNonRedundantTimeouts();
            //calibrate the timeouts
            allTimeoutsAreCalibrated = handleTimeouts(timeouts);

            if (!allTimeoutsAreCalibrated || !lastRound) {
                //if all timeouts aren't calibrated and/or this isn't the last round, it isn't finished
                finished = false;
                //set all values to the repositories
                TimeoutRepository.setAllCalibrationTimeouts(timeouts);
            }else{
                //if all timeouts are calibrated and this was the last round, it is finished
                finished = true;
                //write timeouts to properties
                CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();
                //shutdown SoapUI
                SoapUIShutdownHelper.shutdownSoapUIForReal();
                LOGGER.info("Calibration is finished.");
            }
            //if the last round all timeouts were kept, it's the last round
            lastRound = allTimeoutsAreCalibrated;
            //write all timeouts to csv for traceability
            CalibrationTimeoutRepository.writeToCSV(csv, numberOfDuration);
            //clean the timeoutRepository for the new run
            CalibrationTimeoutRepository.clean();
            //write all timeouts to the console
            for (CalibrationTimeout timeout : timeouts.values()) {
                LOGGER.info(timeout.getKey() + " " + timeout.getStatus() + " " + timeout.getTimeoutInMs());
            }
        }
    }

    private static boolean handleTimeouts(HashMap<String, CalibrationTimeout> timeouts) {
        Objects.requireNonNull(timeouts, "The timeouts can't be null.");
        boolean allTimeoutsAreCalibrated = true;
        for (CalibrationTimeout timeout : timeouts.values()) {
            switch (timeout.getStatus()) {
                case EXCEEDED:
                    timeout.setValue(timeout.getTimeoutInMs() + 5000);
                    allTimeoutsAreCalibrated = false;
                    break;
                case KEPT:
                    break;
            }
            Double value = timeout.getTimeoutInMs() * 1.1;
            timeout.setValue(value.intValue());
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

