package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNAssertions;
import betsy.common.tasks.FileTasks;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class OverlappingTimestampCheckerTests {
    private static final Path ROOT = Paths.get(System.getProperty("user.dir"));
    private static final Path PATH_TO_TEST_FOLDER = Paths.get("src", "test", "groovy", "betsy", "bpmn", "engines");

    private static final Path LOG_FILE = Paths.get("log1.txt");
    private static final Path LOG_1_PARALLEL_ONE = Paths.get("log1_parallelOne.txt");
    private static final Path LOG_1_PARALLEL_TWO = Paths.get("log1_parallelTwo.txt");

    private static final Path PATH_LOG_FILE = ROOT.resolve(PATH_TO_TEST_FOLDER).resolve(LOG_FILE);
    private static final Path PATH_LOG_ONE = ROOT.resolve(PATH_TO_TEST_FOLDER).resolve(LOG_1_PARALLEL_ONE);
    private static final Path PATH_LOG_TWO = ROOT.resolve(PATH_TO_TEST_FOLDER).resolve(LOG_1_PARALLEL_TWO);

    private static final String ONE = "1420110000000";
    private static final String TWO = "1420111800000";
    private static final String THREE = "1420113600000";
    private static final String FOUR = "1420115400000";

    public void createOverlappingLogFilesWithTimestampsOverlapping(String startingTimestampOne, String endingTimestampOne, String startingTimeStampTwo, String endingTimestampTwo) throws IOException {
        String contentLogOne = String.format("%s%s%s", startingTimestampOne, "\n", endingTimestampOne);
        String contentLogTwo = String.format("%s%s%s", startingTimeStampTwo, "\n", endingTimestampTwo);

        System.out.println("Create log files...");
        Files.createFile(PATH_LOG_FILE);
        FileTasks.createFile(PATH_LOG_ONE, contentLogOne);
        FileTasks.createFile(PATH_LOG_TWO, contentLogTwo);
        System.out.println("Creation successful!");
    }

    @Test
    public void testCheckParallelismWhenTimestampsOverlapping() throws Exception {
        createOverlappingLogFilesWithTimestampsOverlapping(ONE, THREE, TWO, FOUR);
        OverlappingTimestampChecker otc = new OverlappingTimestampChecker(PATH_LOG_FILE, PATH_LOG_ONE, PATH_LOG_TWO);
        otc.checkParallelism();
        assertTrue(executionWasParallel());
    }

    @Test
    public void testCheckParallelismWhenTimestampsAreNotOverlapping() throws Exception {
        createOverlappingLogFilesWithTimestampsOverlapping(ONE, TWO, THREE, FOUR);
        OverlappingTimestampChecker otc = new OverlappingTimestampChecker(PATH_LOG_FILE, PATH_LOG_ONE, PATH_LOG_TWO);
        otc.checkParallelism();
        assertTrue(!executionWasParallel());
    }

    @After
    public void deleteLogFiles() throws IOException {
        System.out.println("Delete log files...");
        Files.deleteIfExists(PATH_LOG_FILE);
        Files.deleteIfExists(PATH_LOG_ONE);
        Files.deleteIfExists(PATH_LOG_TWO);
        System.out.println("Deletion successful!");
    }

    public boolean executionWasParallel() {
        List<String> lines = FileTasks.readAllLines(PATH_LOG_FILE);
        return lines.get(0).contains(BPMNAssertions.EXECUTION_PARALLEL.toString());
    }

}
