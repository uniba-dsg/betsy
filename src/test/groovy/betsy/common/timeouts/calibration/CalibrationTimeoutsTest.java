package betsy.common.timeouts.calibration;

import betsy.common.tasks.FileTasks;
import org.junit.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class CalibrationTimeoutsTest {

    private CalibrationTimeouts calibrationTimeouts;
    private CalibrationTimeout calibrationTimeout;
    private CalibrationTimeout calibrationTimeoutTest;
    private ArrayList<CalibrationTimeout> calibrationTimeoutList;
    private File properties;
    private File csv;
    private File testCsv;

    @BeforeClass
    public static void setUpClass() {
        String transitionFolder = "transition_folder";
        FileTasks.mkdirs(Paths.get(transitionFolder));
        File properties = new File("timeout.properties");
        if (properties.exists()) {
            FileTasks.copyFileIntoFolder(properties.toPath(), Paths.get(transitionFolder));
            FileTasks.deleteFile(properties.toPath());
        }
        File csv = new File("calibration_timeouts.csv");
        if (csv.exists()) {
            FileTasks.copyFileIntoFolder(csv.toPath(), Paths.get(transitionFolder));
            FileTasks.deleteFile(csv.toPath());
        }
    }

    @AfterClass
    public static void tearDownClass() {
        File properties = new File("timeout.properties");
        File csv = new File("calibration_timeouts.csv");
        FileTasks.deleteFile(properties.toPath());
        FileTasks.deleteFile(csv.toPath());
        String transitionFolder = "transition_folder";
        Path currentRelativePath = Paths.get("");
        FileTasks.copyFilesInFolderIntoOtherFolder(Paths.get(transitionFolder), currentRelativePath);
        FileTasks.deleteDirectory(Paths.get(transitionFolder));
    }

    @Before
    public void setUp() throws Exception {
        calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeoutTest = new CalibrationTimeout("tomcat", "startup", "maven", 30_000, 4_000);
        calibrationTimeoutList = new ArrayList<>();
        properties = new File("timeout.properties");
        csv = new File("calibration_timeouts");
        testCsv = new File(csv.getName() + ".csv");
        calibrationTimeouts = new CalibrationTimeouts(calibrationTimeoutList, properties.getName(), csv.getName());
    }

    @After
    public void tearDown() throws Exception {
        calibrationTimeouts = null;
        calibrationTimeout = null;
        calibrationTimeoutTest = null;
        calibrationTimeoutList = null;
        properties.delete();
        properties = null;
        testCsv.delete();
        testCsv = null;
        csv = null;
    }

    @Test
    public void testConstructor() throws Exception {
        calibrationTimeouts = new CalibrationTimeouts();
        calibrationTimeouts.writeAllCalibrationTimeoutsToProperties();
        assertTrue(properties.exists());
        calibrationTimeouts.writeToCSV();
        assertTrue(testCsv.exists());
        assertNotNull(calibrationTimeouts.getAllCalibrationTimeouts());
        assertEquals(0, calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testConstructorOwnTimeoutsPropertiesCSV() throws Exception {
        calibrationTimeouts = new CalibrationTimeouts(calibrationTimeoutList, properties.getName(), csv.getName());
        calibrationTimeouts.writeAllCalibrationTimeoutsToProperties();
        assertTrue(properties.exists());
        calibrationTimeouts.writeToCSV();
        assertTrue(testCsv.exists());
        assertNotNull(calibrationTimeouts.getAllCalibrationTimeouts());
        assertEquals(calibrationTimeoutList.size(), calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testConstructorOwnPropertiesCSV() throws Exception {
        calibrationTimeouts = new CalibrationTimeouts(properties.getName(), csv.getName());
        calibrationTimeouts.writeAllCalibrationTimeoutsToProperties();
        assertTrue(properties.exists());
        calibrationTimeouts.writeToCSV();
        assertTrue(testCsv.exists());
        assertNotNull(calibrationTimeouts.getAllCalibrationTimeouts());
        assertEquals(0, calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testConstructorOwnTimeouts() throws Exception {
        calibrationTimeouts = new CalibrationTimeouts(calibrationTimeoutList);
        assertNotNull(calibrationTimeouts.getAllCalibrationTimeouts());
        assertEquals(calibrationTimeoutList.size(), calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testAddCalibrationTimeout() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        assertEquals(calibrationTimeout, calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).get());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).get().getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).get().getTimeToRepetitionInMs());
    }

    @Test
    public void testAddCalibrationTimeoutNull() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(null);
    }

    @Test
    public void testGetCalibrationTimeout() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        assertEquals(calibrationTimeout, calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).get());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).get().getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).get().getTimeToRepetitionInMs());
    }

    @Test
    public void testGetAllCalibrationTimeouts() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeoutTest);
        HashMap<String, CalibrationTimeout> timeouts = calibrationTimeouts.getAllCalibrationTimeouts();
        assertEquals(calibrationTimeout, timeouts.get(calibrationTimeout.getCalibrationTimeoutKey()));
        assertEquals(calibrationTimeoutTest, timeouts.get(calibrationTimeoutTest.getCalibrationTimeoutKey()));
    }

    @Test
    public void testWriteAllCalibrationTimeoutsToProperties() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        calibrationTimeouts.writeAllCalibrationTimeoutsToProperties();
        assertTrue(properties.exists());
    }

    @Test
    public void testWriteToCSV() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        calibrationTimeouts.writeToCSV();
        assertTrue(testCsv.exists());
    }

    @Test
    public void testClean() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        calibrationTimeouts.clean();
        assertEquals(0, calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testGetAllNonRedundantTimeouts() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        //to secure, that the timestamps are different
        Thread.sleep(1);
        CalibrationTimeout calibrationTimeoutTest = new CalibrationTimeout("ode", "deploy", "maven", 70_000, 5_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeoutTest);
        HashMap<String, CalibrationTimeout> timeouts = calibrationTimeouts.getAllNonRedundantTimeouts();
        assertEquals(calibrationTimeoutTest.getTimeoutInMs(), timeouts.get(calibrationTimeout.getKey()).getTimeoutInMs());
    }

    @Test
    public void testGetAllNonRedundantTimeoutsStatusExceeded() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        //to secure, that the timestamps are different
        Thread.sleep(1);
        CalibrationTimeout secondCalibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 30_000, 5_000);
        secondCalibrationTimeout.setStatus(CalibrationTimeout.Status.EXCEEDED);
        calibrationTimeouts.addCalibrationTimeout(secondCalibrationTimeout);
        HashMap<String, CalibrationTimeout> timeouts = calibrationTimeouts.getAllNonRedundantTimeouts();
        assertEquals(secondCalibrationTimeout.getTimeoutInMs(), timeouts.get(calibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals(secondCalibrationTimeout.getStatus(), timeouts.get(calibrationTimeout.getKey()).getStatus());
    }
}

