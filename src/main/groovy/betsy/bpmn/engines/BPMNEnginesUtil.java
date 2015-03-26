package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stlmaass on 19.03.2015.
 */
public class BPMNEnginesUtil {

    private static final Logger LOGGER = Logger.getLogger(BPMNEnginesUtil.class);

    public static void substituteSpecificErrorsForGenericError(BPMNTestCase testCase, Path logDir) {
        if(testCase.getAssertions().contains(BPMNAssertions.ERROR_GENERIC.toString())) {
            List<String> toReplace = new ArrayList<>();
            toReplace.add(BPMNAssertions.ERROR_DEPLOYMENT.toString());
            toReplace.add(BPMNAssertions.ERROR_RUNTIME.toString());
            FileTasks.replaceLogFileContent(toReplace, BPMNAssertions.ERROR_GENERIC.toString(), logDir);
        }
    }

    public static void checkParallelExecution(BPMNTestCase testCase, Path logFile) {
        // Only check on parallelism when asked for a parallel assertion
        if (!testCase.getAssertions().contains(BPMNAssertions.EXECUTION_PARALLEL.toString())) {
            return;
        }

        String testCaseNumber = String.valueOf(testCase.getNumber());
        Path logParallelOne = logFile.getParent().resolve("log" + testCaseNumber + "_parallelOne.txt");
        Path logParallelTwo = logFile.getParent().resolve("log" + testCaseNumber + "_parallelTwo.txt");

        try {
            OverlappingTimestampChecker otc = new OverlappingTimestampChecker(logFile, logParallelOne, logParallelTwo);
            otc.checkParallelism();
        } catch (IllegalArgumentException e) {
            LOGGER.info("Could not validate parallel execution", e);
        }

    }

    public static void checkDataLog(BPMNTestCase testCase, Path logFile) {
        // Only check when asked for a data type assertion
        if (!testCase.getAssertions().contains(BPMNAssertions.DATA_CORRECT.toString())) {
            return;
        }

        Path dataLog = Paths.get(logFile.toString().replaceAll("\\.txt", "_data.txt"));

        try {
            DataLogChecker dlc = new DataLogChecker(logFile, dataLog);
            dlc.checkDataTypes();
        } catch (IllegalArgumentException e) {
            LOGGER.info("Cloud not evaluate data log");
        }
    }

}
