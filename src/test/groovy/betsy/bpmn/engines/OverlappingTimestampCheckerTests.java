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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OverlappingTimestampCheckerTests {
    private static final Path ROOT = Paths.get(System.getProperty("user.dir"), "src", "test", "temp");

    private static final Path PATH_LOG_FILE = ROOT.resolve("log1.txt");
    private static final Path PATH_LOG_ONE = ROOT.resolve("log1_parallelOne.txt");
    private static final Path PATH_LOG_TWO = ROOT.resolve("log1_parallelTwo.txt");

    private static final String[] TIMESTAMPS = {"1420110000000", "1420111800000", "1420113600000", "1420115400000"};

    public void createLogFilesWithTimestamps(int first, int second, int third, int fourth) throws IOException {
        String contentLogOne = String.format("%s%n%s", TIMESTAMPS[first], TIMESTAMPS[second]);
        String contentLogTwo = String.format("%s%n%s", TIMESTAMPS[third], TIMESTAMPS[fourth]);

        System.out.println("Create log files...");
        FileTasks.createFile(PATH_LOG_ONE, contentLogOne);
        FileTasks.createFile(PATH_LOG_TWO, contentLogTwo);
        Files.createFile(PATH_LOG_FILE); // needs to be executed after the two lines before
        System.out.println("Creation successful!");
    }

    @Test
    public void testCheckParallelismWithOneOverlapsTwo() throws Exception {
        createLogFilesWithTimestamps(0, 2, 1, 3);
        OverlappingTimestampChecker otc = new OverlappingTimestampChecker(PATH_LOG_FILE, PATH_LOG_ONE, PATH_LOG_TWO);
        otc.checkParallelism();
        assertTrue(executionWasParallel());
    }

    @Test
    public void testCheckParallelismWithOneBeforeTwo() throws Exception {
        createLogFilesWithTimestamps(0, 1, 2, 3);
        OverlappingTimestampChecker otc = new OverlappingTimestampChecker(PATH_LOG_FILE, PATH_LOG_ONE, PATH_LOG_TWO);
        otc.checkParallelism();
        assertFalse(executionWasParallel());
    }

    @Test
    public void testCheckParallelismWithOneEqualsTwo() throws Exception {
        createLogFilesWithTimestamps(0, 1, 0, 1);
        OverlappingTimestampChecker otc = new OverlappingTimestampChecker(PATH_LOG_FILE, PATH_LOG_ONE, PATH_LOG_TWO);
        otc.checkParallelism();
        assertTrue(executionWasParallel());
    }

    @Test
    public void testCheckParallelismWithOneDuringTwo() throws Exception {
        createLogFilesWithTimestamps(1, 2, 0, 3);
        OverlappingTimestampChecker otc = new OverlappingTimestampChecker(PATH_LOG_FILE, PATH_LOG_ONE, PATH_LOG_TWO);
        otc.checkParallelism();
        assertTrue(executionWasParallel());
    }

    @Test
    public void testCheckParallelismWithOneAfterTwo() throws Exception {
        createLogFilesWithTimestamps(2, 3, 0, 1);
        OverlappingTimestampChecker otc = new OverlappingTimestampChecker(PATH_LOG_FILE, PATH_LOG_ONE, PATH_LOG_TWO);
        otc.checkParallelism();
        assertFalse(executionWasParallel());
    }

    @After
    public void deleteLogFiles() throws IOException {
        System.out.println("Delete log files...");
        Files.deleteIfExists(PATH_LOG_FILE);
        Files.deleteIfExists(PATH_LOG_ONE);
        Files.deleteIfExists(PATH_LOG_TWO);
        Files.deleteIfExists(PATH_LOG_FILE.getParent());
        System.out.println("Deletion successful!");
    }

    public boolean executionWasParallel() {
        List<String> lines = FileTasks.readAllLines(PATH_LOG_FILE);
        return !lines.isEmpty() && lines.get(0).contains(BPMNAssertions.EXECUTION_PARALLEL.toString());
    }

}
