package betsy.common.timeouts.calibration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import betsy.common.tasks.FileTasks;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class CalibrationTimeoutsTest {

    private CalibrationTimeouts calibrationTimeouts;
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
        properties = new File("timeout.properties");
        csv = new File("calibration_timeouts");
        testCsv = new File(csv.getName() + ".csv");
        calibrationTimeouts = new CalibrationTimeouts();
    }

    @After
    public void tearDown() throws Exception {
        calibrationTimeouts = null;
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
        assertTrue("The properties should exist.",properties.exists());
        calibrationTimeouts.writeToCSV();
        assertTrue("The csv file should exist.", testCsv.exists());
        assertNotNull("The value should not be null.", calibrationTimeouts.getAllCalibrationTimeouts());
        assertEquals("After creation there shouldn't exists values.", 0, calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testConstructorOwnTimeoutsPropertiesCSV() throws Exception {
        ArrayList<CalibrationTimeout> calibrationTimeoutList = new ArrayList<>();
        calibrationTimeouts = new CalibrationTimeouts(calibrationTimeoutList, properties.getName(), csv.getName());
        calibrationTimeouts.writeAllCalibrationTimeoutsToProperties();
        assertTrue("The properties should exist.", properties.exists());
        calibrationTimeouts.writeToCSV();
        assertTrue("The csv file should exist.", testCsv.exists());
        assertNotNull("The timeoutList shouldn't not be null",  calibrationTimeouts.getAllCalibrationTimeouts());
        assertEquals("The sizes have to be equal.", calibrationTimeoutList.size(), calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testConstructorOwnPropertiesCSV() throws Exception {
        calibrationTimeouts = new CalibrationTimeouts(properties.getName(), csv.getName());
        calibrationTimeouts.writeAllCalibrationTimeoutsToProperties();
        assertTrue("The properties should exist.",  properties.exists());
        calibrationTimeouts.writeToCSV();
        assertTrue("The csv file should exist.",  testCsv.exists());
        assertNotNull("The timeoutList shouldn't be null.", calibrationTimeouts.getAllCalibrationTimeouts());
        assertEquals("After creation there shouldn't exists values.", 0, calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testConstructorOwnTimeouts() throws Exception {
        ArrayList<CalibrationTimeout> calibrationTimeoutList = new ArrayList<>();
        calibrationTimeouts = new CalibrationTimeouts(calibrationTimeoutList);
        assertNotNull("The value shouldn't be null.", calibrationTimeouts.getAllCalibrationTimeouts());
        assertEquals("The values sizes  be equal.", calibrationTimeoutList.size(), calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testAddCalibrationTimeout() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        assertEquals("The keys should be equal.", calibrationTimeout, calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()));
        assertEquals("The timeouts in ms should be equal.", calibrationTimeout.getTimeoutInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).getTimeoutInMs());
        assertEquals("The timeToRepetition in ms should be equal.", calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testAddCalibrationTimeoutNull() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(null);
    }

    @Test
    public void testGetCalibrationTimeout() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        assertEquals("The keys should be equal.", calibrationTimeout, calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()));
        assertEquals("The timeouts in ms should be equal.", calibrationTimeout.getTimeoutInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).getTimeoutInMs());
        assertEquals("The timeToRepetition in ms should be equal.", calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getCalibrationTimeoutKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testGetAllCalibrationTimeouts() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        CalibrationTimeout calibrationTimeoutTest = new CalibrationTimeout("tomcat", "startup", "maven", 30_000, 4_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeoutTest);
        HashMap<String, CalibrationTimeout> timeouts = calibrationTimeouts.getAllCalibrationTimeouts();
        assertEquals("The values should be equal.", calibrationTimeout, timeouts.get(calibrationTimeout.getCalibrationTimeoutKey()));
        assertEquals("The values should be equal.", calibrationTimeoutTest, timeouts.get(calibrationTimeoutTest.getCalibrationTimeoutKey()));
    }

    @Test
    public void testWriteAllCalibrationTimeoutsToProperties() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        calibrationTimeouts.writeAllCalibrationTimeoutsToProperties();
        assertTrue("The properties should exist.", properties.exists());
    }

    @Test
    public void testWriteToCSVParameters() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        Path csv = Paths.get("calibration_timeouts.csv");
        calibrationTimeouts.writeToCSV(csv, 0);
        assertTrue("The csv file should exist.", csv.toFile().exists());
    }

    @Test
    public void testWriteToCSV() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        calibrationTimeouts.writeToCSV();
        assertTrue("The csv file should exist.",  testCsv.exists());
    }

    @Test
    public void testClean() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        calibrationTimeouts.clean();
        assertEquals("After cleaning there shouldn't be values.", 0, calibrationTimeouts.getAllCalibrationTimeouts().size());
    }

    @Test
    public void testGetAllNonRedundantTimeouts() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        //to secure, that the timestamps are different
        Thread.sleep(1);
        CalibrationTimeout calibrationTimeoutTest = new CalibrationTimeout("ode", "deploy", "maven", 70_000, 5_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeoutTest);
        HashMap<String, CalibrationTimeout> timeouts = calibrationTimeouts.getAllNonRedundantTimeouts();
        assertEquals("The keys should be equal.", calibrationTimeoutTest.getKey(), timeouts.get(calibrationTimeout.getKey()).getKey());
    }

    @Test
    public void testGetAllNonRedundantTimeoutsStatusExceeded() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        //to secure, that the timestamps are different
        Thread.sleep(1);
        CalibrationTimeout secondCalibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 30_000, 5_000);
        secondCalibrationTimeout.setStatus(CalibrationTimeout.Status.EXCEEDED);
        calibrationTimeouts.addCalibrationTimeout(secondCalibrationTimeout);
        HashMap<String, CalibrationTimeout> timeouts = calibrationTimeouts.getAllNonRedundantTimeouts();
        assertEquals("The timeouts in ms should be equal.", secondCalibrationTimeout.getTimeoutInMs(), timeouts.get(calibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals("The statuses should be equal.", secondCalibrationTimeout.getStatus(), timeouts.get(calibrationTimeout.getKey()).getStatus());
    }
}

