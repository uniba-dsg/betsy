package betsy.common.timeouts;

import betsy.Main;
import betsy.bpel.BPELMain;
import betsy.bpel.soapui.SoapUIShutdownHelper;
import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.calibration.CalibrationTimeout;
import betsy.common.timeouts.calibration.CalibrationTimeoutRepository;
import betsy.common.timeouts.timeout.Timeout;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        File properties = new File("timeout.properties");
        File csv = new File("calibration_timeouts.csv");
        FileTasks.deleteFile(csv.toPath());


        while (numberOfDuration < 4) {
            //execute betsy
            Main.main(addChangedTestFolderToArgs(args, numberOfDuration));
            if (numberOfDuration > 0) {
                //write all timeouts to csv for traceability
                CalibrationTimeoutRepository.writeToCSV(csv, numberOfDuration++);
                CalibrationTimeoutRepository.clean();
            } else {
                numberOfDuration++;
                CalibrationTimeoutRepository.clean();
            }
        }

        while (!finished) {
            //execute betsy
            Main.main(addChangedTestFolderToArgs(args, numberOfDuration));
            //get used timeouts
            HashMap<String, CalibrationTimeout> timeouts = CalibrationTimeoutRepository.getAllNonRedundantTimeouts();
            //calibrate the timeouts
            allTimeoutsAreCalibrated = handleTimeouts(timeouts, csv);

            if (!allTimeoutsAreCalibrated || !lastRound) {
                if (allTimeoutsAreCalibrated) {
                    //write timeouts to properties
                    TimeoutIOOperations.writeToProperties(properties, new ArrayList<>(timeouts.values()));
                }
                //if all timeouts aren't calibrated and/or this isn't the last round, it isn't finished
                finished = false;
                //set all values to the repositories
                TimeoutRepository.setAllCalibrationTimeouts(timeouts);
            } else {
                //if all timeouts are calibrated and this was the last round, it is finished
                finished = true;
                //shutdown SoapUI
                SoapUIShutdownHelper.shutdownSoapUIForReal();
                LOGGER.info("Calibration is finished.");
            }
            //if the last round all timeouts were kept, it's the last round
            lastRound = allTimeoutsAreCalibrated;
            //write all timeouts to csv for traceability
            CalibrationTimeoutRepository.writeToCSV(csv, numberOfDuration++);
            //clean the timeoutRepository for the new run
            CalibrationTimeoutRepository.clean();
            //write all timeouts to the console
            for (CalibrationTimeout timeout : timeouts.values()) {
                LOGGER.info(timeout.getKey() + " " + timeout.getStatus() + " " + timeout.getTimeoutInMs());
            }
        }
    }

    private static boolean handleTimeouts(HashMap<String, CalibrationTimeout> timeouts, File csv) {
        Objects.requireNonNull(timeouts, "The timeouts can't be null.");
        boolean allTimeoutsAreCalibrated = true;
        for (CalibrationTimeout timeout : timeouts.values()) {
            switch (timeout.getStatus()) {
                case EXCEEDED:
                    timeout.setValue(calculatedTimeout(timeout, 2, csv));
                    allTimeoutsAreCalibrated = false;
                    break;
                case KEPT:
                    timeout.setValue(calculatedTimeout(timeout, 2, csv));
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

    private static int calculateExpectation(List<CalibrationTimeout> timeouts) {
        int expectation = 0;
        for (CalibrationTimeout timeoutValue : timeouts) {
            expectation = expectation + timeoutValue.getMeasuredTime();
        }
        return expectation / timeouts.size();
    }

    private static double calculateVariance(List<CalibrationTimeout> timeouts, int expectation) {
        double standardVariance = 0;
        for (CalibrationTimeout timeoutValue : timeouts) {
            standardVariance = standardVariance + Math.pow(timeoutValue.getMeasuredTime() - expectation, 2);
        }
        return standardVariance / timeouts.size();
    }

    private static double standardDeviation(List<CalibrationTimeout> timeouts, int expectation) {
        return Math.sqrt(calculateVariance(timeouts, expectation));
    }

    private static int calculatedTimeout(Timeout timeout, int x, File csv) {
        List<CalibrationTimeout> timeouts = TimeoutIOOperations.readFromCSV(csv).stream().filter(actualTimeout -> actualTimeout.getKey().equals(timeout.getKey())).collect(Collectors.toList());
        int expectation = calculateExpectation(timeouts);
        return expectation + (x * new Double(standardDeviation(timeouts, expectation)).intValue());
    }
}

