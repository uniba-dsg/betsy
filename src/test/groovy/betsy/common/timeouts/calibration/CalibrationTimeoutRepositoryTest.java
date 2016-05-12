package betsy.common.timeouts.calibration;

import betsy.common.tasks.FileTasks;
import org.junit.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class CalibrationTimeoutRepositoryTest {

    private CalibrationTimeout calibrationTimeout;


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
        calibrationTimeout = new CalibrationTimeout("ode", "deploy", "install", 50_000, 5_000);
    }

    @After
    public void tearDown() throws Exception {
        calibrationTimeout = null;
        CalibrationTimeoutRepository.clean();
    }

    @Test
    public void testGetAllCalibrationTimeouts() throws Exception {
        CalibrationTimeout secondCalibrationTimeout = new CalibrationTimeout("tomcat", "startup", "install", 50_000, 5_000);
        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
        CalibrationTimeoutRepository.addCalibrationTimeout(secondCalibrationTimeout);
        HashMap<String, CalibrationTimeout> calibrationTimeoutList = CalibrationTimeoutRepository.getAllCalibrationTimeouts();
        assertEquals("The keys should be equal.", calibrationTimeout.getKey(), calibrationTimeoutList.get(calibrationTimeout.getCalibrationTimeoutKey()).getKey());
        assertEquals("The timeouts in ms should be equal.",calibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(calibrationTimeout.getCalibrationTimeoutKey()).getTimeoutInMs());
        assertEquals("The timeToRepetitions should be equal.", calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(calibrationTimeout.getCalibrationTimeoutKey()).getTimeToRepetitionInMs());
        assertEquals("The keys should be equal.", secondCalibrationTimeout.getKey(), calibrationTimeoutList.get(secondCalibrationTimeout.getCalibrationTimeoutKey()).getKey());
        assertEquals("The timeouts in ms should be equal.", secondCalibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(secondCalibrationTimeout.getCalibrationTimeoutKey()).getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", secondCalibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(secondCalibrationTimeout.getCalibrationTimeoutKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testAddTimeout() throws Exception {
        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
        HashMap<String, CalibrationTimeout> calibrationTimeoutList = CalibrationTimeoutRepository.getAllCalibrationTimeouts();
        assertEquals("The keys should be equal.", calibrationTimeout.getKey(), calibrationTimeoutList.get(calibrationTimeout.getCalibrationTimeoutKey()).getKey());
        assertEquals("The timeouts in ms should be equal.", calibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(calibrationTimeout.getCalibrationTimeoutKey()).getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(calibrationTimeout.getCalibrationTimeoutKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testWriteAllCalibrationTimeoutsToProperties() throws Exception {
        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
        CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();
        File properties = new File("timeout.properties");
        assertTrue("The properties file should exists.", properties.exists());
    }

    @Test
    public void testWriteToCSVParameters() throws Exception {
        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
        CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();
        Path csv = Paths.get("calibration_timeouts.csv");
        CalibrationTimeoutRepository.writeToCSV(csv, 1);
        assertTrue("The csv file should exists.", csv.toFile().exists());
    }

    @Test
    public void testWriteToCSV() throws Exception {
        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
        CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();
        CalibrationTimeoutRepository.writeToCSV();
        File file = new File("calibration_timeouts.csv");
        assertTrue("The csv file should exists.", file.exists());
    }

    @Test
    public void testClean() throws Exception {
        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
        CalibrationTimeoutRepository.clean();
        assertEquals("After cleaning the size should be null.", 0, CalibrationTimeoutRepository.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testGetAllNonRedundantTimeouts() throws Exception {
        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
        //to secure, that the timestamps are different
        Thread.sleep(1);
        CalibrationTimeout calibrationTimeoutTest = new CalibrationTimeout("ode", "deploy", "install", 70_000, 5_000);
        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeoutTest);
        HashMap<String, CalibrationTimeout> timeouts = CalibrationTimeoutRepository.getAllNonRedundantTimeouts();
        assertEquals("The keys should be equal.", calibrationTimeoutTest.getKey(), timeouts.get(calibrationTimeout.getKey()).getKey());
    }

    @Test
    public void testGetAllNonRedundantTimeoutsStatusExceeded() throws Exception {
        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
        //to secure, that the timestamps are different
        Thread.sleep(1);
        CalibrationTimeout secondCalibrationTimeout = new CalibrationTimeout("ode", "deploy", "install", 30_000, 5_000);
        secondCalibrationTimeout.setStatus(CalibrationTimeout.Status.EXCEEDED);
        CalibrationTimeoutRepository.addCalibrationTimeout(secondCalibrationTimeout);
        HashMap<String, CalibrationTimeout> timeouts = CalibrationTimeoutRepository.getAllNonRedundantTimeouts();
        assertEquals("The timeouts in ms should be equal.", secondCalibrationTimeout.getTimeoutInMs(), timeouts.get(calibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals("The statuses should be equal.", secondCalibrationTimeout.getStatus(), timeouts.get(calibrationTimeout.getKey()).getStatus());
    }
}

