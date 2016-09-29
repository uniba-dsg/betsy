package betsy.bpmn.engines;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import betsy.bpmn.model.BPMNAssertions;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;
import pebl.benchmark.test.TestCase;

public class BPMNEnginesUtil {

    private static final Logger LOGGER = Logger.getLogger(BPMNEnginesUtil.class);

    public static void substituteSpecificErrorsForGenericError(TestCase testCase, Path logDir) {
        if (TestCaseUtil.getTraceAssertions(testCase).contains(BPMNAssertions.ERROR_GENERIC.toString())){
            List<String> toReplace = new ArrayList<>();
            toReplace.add(BPMNAssertions.ERROR_DEPLOYMENT.toString());
            toReplace.add(BPMNAssertions.ERROR_RUNTIME.toString());
            FileTasks.replaceLogFileContent(toReplace, BPMNAssertions.ERROR_GENERIC.toString(), logDir);
        }
    }

    public static void checkParallelExecution(TestCase testCase, Path logFile) {
        // Only check on parallelism when asked for a parallel assertion
        if (!TestCaseUtil.getTraceAssertions(testCase).contains(BPMNAssertions.EXECUTION_PARALLEL.toString())) {
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

    public static void checkMarkerFileExists(TestCase testCase, Path logFile) {
        if (!TestCaseUtil.getTraceAssertions(testCase).contains(BPMNAssertions.MARKER_EXISTS.toString())) {
            return;
        }
        if (Files.exists(logFile.getParent().resolve("MARKER"))) {
            BPMNAssertions.appendToFile(logFile, BPMNAssertions.MARKER_EXISTS);
        }
    }

    public static void checkDataLog(TestCase testCase, Path logFile) {
        // Only check when asked for a data type assertion
        if (!TestCaseUtil.getTraceAssertions(testCase).contains(BPMNAssertions.DATA_CORRECT.toString())) {
            return;
        }

        Path dataLog = Paths.get(logFile.toString().replaceAll("\\.txt", "_data.txt"));

        try {
            DataLogChecker dlc = new DataLogChecker(logFile, dataLog, "String");
            dlc.checkDataTypes();
        } catch (IllegalArgumentException e) {
            LOGGER.info("Cloud not evaluate data log");
        }
    }

}
