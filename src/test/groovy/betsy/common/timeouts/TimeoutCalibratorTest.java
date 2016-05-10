package betsy.common.timeouts;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.calibration.CalibrationTimeout;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutCalibratorTest {

    private TestAppender testAppender;
    private static final Logger LOGGER = Logger.getLogger(TimeoutCalibrator.class);

    @BeforeClass
    public static void setUpClass() {
        String transitionFolder = "transition_folder";
        FileTasks.mkdirs(Paths.get(transitionFolder));
        Path csv = Paths.get("calibration_timeouts.csv");
        if (csv.toFile().exists()) {
            FileTasks.copyFileIntoFolder(csv, Paths.get(transitionFolder));
            FileTasks.deleteFile(csv);
        }
    }

    @AfterClass
    public static void tearDownClass() {
        Path csv = Paths.get("calibration_timeouts.csv");
        FileTasks.deleteFile(csv);
        String transitionFolder = "transition_folder";
        Path currentRelativePath = Paths.get("");
        FileTasks.copyFilesInFolderIntoOtherFolder(Paths.get(transitionFolder), currentRelativePath);
        FileTasks.deleteDirectory(Paths.get(transitionFolder));
    }

    @Before
    public void setUp() throws Exception {
        testAppender = new TestAppender();
        LOGGER.addAppender(testAppender);
    }

    @After
    public void tearDown() throws Exception {
        LOGGER.removeAllAppenders();
        testAppender = null;
    }

    @Test
    public void testDetermineTimeouts() throws Exception {
        Path csv = Paths.get("calibration_timeouts.csv");
        List<CalibrationTimeout> timeouts = new ArrayList<>();
        CalibrationTimeout timeoutFirst = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        timeoutFirst.setMeasuredTime(10_000);
        timeouts.add(timeoutFirst);
        CalibrationTimeout timeoutSecond = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        timeoutSecond.setMeasuredTime(40_000);
        timeouts.add(timeoutSecond);
        CalibrationTimeout timeoutThird = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        timeoutThird.setMeasuredTime(50_000);
        timeouts.add(timeoutThird);
        CalibrationTimeout timeoutFourth = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        timeoutFourth.setMeasuredTime(20_000);
        timeouts.add(timeoutFourth);
        CSV.write(csv, timeouts);

        CalibrationTimeout timeout = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        HashMap<String, CalibrationTimeout> hashMap = new HashMap<>();
        hashMap.put("openesb_v.deploymentTimeout", timeout);
        TimeoutCalibrator.determineTimeouts(hashMap, csv);
        assertEquals("The value is the sum of the 2-fold standardDeviation and the expectation.", 61622, hashMap.get("openesb_v.deploymentTimeout").getTimeoutInMs());
    }

    @Test
    public void testDetermineTimeoutsTimeoutsSmallerNull() throws Exception {
        HashMap<String, CalibrationTimeout> hashMap = new HashMap<>();
        TimeoutCalibrator.determineTimeouts(hashMap, Paths.get("calibration_timeouts.csv"));
        assertEquals("The number of the timeouts has to be greater than null to determine the timeouts.", testAppender.messages.get(0));
    }

    @Test
    public void testAddChangedTestFolderToArgs() throws Exception {
        String[] args = {"calibrate", "bpel", "ode", "sequence"};
        int i = 0;
        String[] result = TimeoutCalibrator.addChangedTestFolderToArgs(args, i);
        assertEquals(args[0], result[0]);
        assertEquals("-ftest/test" + i, result[1]);
        assertEquals("The args should be equal.", args[1], result[2]);
        assertEquals("The args should be equal.", args[2], result[3]);
        assertEquals("The args should be equal.", args[3], result[4]);
    }

    @Test
    public void testAddChangedTestFolderToArgsToShortArgs() throws Exception {
        String[] args = new String[0];
        int i = 0;
        TimeoutCalibrator.addChangedTestFolderToArgs(args, i);
        assertEquals("Can't add test folder to args, because the args aren't greater than null.", testAppender.messages.get(0));
    }

    @Test
    public void testCalculateExpectation() throws Exception {
        List<CalibrationTimeout> timeouts = new ArrayList<>();
        timeouts.add(new CalibrationTimeout(TimeoutRepository.getTimeout("Bpelg.deploy"), 20000));
        timeouts.add(new CalibrationTimeout(TimeoutRepository.getTimeout("Bpelg.deploy"), 40000));
        assertEquals("The value should be the expectation of the given timeouts.", 30000, TimeoutCalibrator.calculateExpectation(timeouts));
    }

    @Test
    public void testCalculateExpectationTooLittleTimeouts() throws Exception {
        TimeoutCalibrator.calculateExpectation(new ArrayList<>());
        assertEquals("The number of the timeouts has to be greater than null to calculate the expectation.", testAppender.messages.get(0));

    }

    @Test
    public void testCalculateVariance() throws Exception {
        List<CalibrationTimeout> timeouts = new ArrayList<>();
        timeouts.add(new CalibrationTimeout(TimeoutRepository.getTimeout("Bpelg.deploy"), 20000));
        timeouts.add(new CalibrationTimeout(TimeoutRepository.getTimeout("Bpelg.deploy"), 40000));
        assertEquals("The value should be the variance of the given timeouts.",  1.0E8, TimeoutCalibrator.calculateVariance(timeouts, TimeoutCalibrator.calculateExpectation(timeouts)), 0);
    }

    @Test
    public void testCalculateVarianceTooLittleTimeouts() throws Exception {
        TimeoutCalibrator.calculateVariance(new ArrayList<>() , 5);
        assertEquals("The number of the timeouts has to be greater than null to calculate the variance.", testAppender.messages.get(0));
    }

    @Test
    public void testStandardDeviation() throws Exception {
        List<CalibrationTimeout> timeouts = new ArrayList<>();
        timeouts.add(new CalibrationTimeout(TimeoutRepository.getTimeout("Bpelg.deploy"), 20000));
        timeouts.add(new CalibrationTimeout(TimeoutRepository.getTimeout("Bpelg.deploy"), 40000));
        assertEquals("The value should be the deviation of the given timeouts.", 10000.0, TimeoutCalibrator.standardDeviation(timeouts, TimeoutCalibrator.calculateExpectation(timeouts)), 0);
    }

    @Test
    public void testStandardDeviationToLittleTimeouts() throws Exception {
        TimeoutCalibrator.standardDeviation(new ArrayList<>(), 5);
        assertEquals("The number of the timeouts has to be greater than null to calculate the standardDeviation.", testAppender.messages.get(0));
    }

    @Test
    public void testCalculatedTimeout() throws Exception {
        Path csv = Paths.get("calibration_timeouts.csv");
        List<CalibrationTimeout> timeouts = new ArrayList<>();
        CalibrationTimeout timeoutFirst = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        timeoutFirst.setMeasuredTime(10_000);
        timeouts.add(timeoutFirst);
        CalibrationTimeout timeoutSecond = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        timeoutSecond.setMeasuredTime(40_000);
        timeouts.add(timeoutSecond);
        CalibrationTimeout timeoutThird = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        timeoutThird.setMeasuredTime(50_000);
        timeouts.add(timeoutThird);
        CalibrationTimeout timeoutFourth = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        timeoutFourth.setMeasuredTime(20_000);
        timeouts.add(timeoutFourth);
        CSV.write(csv, timeouts);
        CalibrationTimeout timeout = new CalibrationTimeout("openesb_v", "deploymentTimeout", 30_000, 500);
        assertEquals("The value is the sum of the 2-fold standardDeviation and the expectation.", 61622, TimeoutCalibrator.calculateTimeout(timeout, 2, csv));
    }

    class TestAppender extends AppenderSkeleton {
        public List<String> messages = new ArrayList<>();

        @Override
        protected void append(LoggingEvent event) {
            messages.add(event.getMessage().toString());
        }

        @Override
        public void close() {
            messages = null;
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    }
}