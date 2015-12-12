package timeouts.calibration_timeout;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Before
    public void setUp() throws Exception {
        calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 20_000, 2_000);
        calibrationTimeoutTest = new CalibrationTimeout("tomcat", "startup", "maven", 30_000, 4_000);
        calibrationTimeoutList = new ArrayList<>();
        properties = new File("test_calibration_timeout.properties");
        csv = new File("test_calibration_timeouts");
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
        properties = new File("timeout.properties");
        csv = new File("calibration_timeouts");
        testCsv = new File(csv.getName() + ".csv");
        if(!properties.exists() &  !testCsv.exists()){
            calibrationTimeouts.writeAllCalibrationTimeoutsToProperties();
            assertTrue(properties.exists());
            calibrationTimeouts.writeToCSV();
            assertTrue(testCsv.exists());
            assertNotNull(calibrationTimeouts.getAllCalibrationTimeouts());
            assertEquals(0, calibrationTimeouts.getAllCalibrationTimeouts().size());
        }else{
            properties = new File("test_calibration_timeout.properties");
            csv = new File("test_calibration_timeouts");
            testCsv = new File(csv.getName() + ".csv");
        }
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
        assertEquals(calibrationTimeout, calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getKey()).get());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getKey()).get().getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getKey()).get().getTimeToRepetitionInMs());
    }

    @Test
    public void testGetCalibrationTimeout() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        assertEquals(calibrationTimeout, calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getKey()).get());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getKey()).get().getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeouts.getCalibrationTimeout(calibrationTimeout.getKey()).get().getTimeToRepetitionInMs());
    }

    @Test
    public void testGetAllCalibrationTimeouts() throws Exception {
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeout);
        calibrationTimeouts.addCalibrationTimeout(calibrationTimeoutTest);
        HashMap<String, CalibrationTimeout> timeouts = calibrationTimeouts.getAllCalibrationTimeouts();
        assertEquals(calibrationTimeout, timeouts.get(calibrationTimeout.getKey()));
        assertEquals(calibrationTimeoutTest, timeouts.get(calibrationTimeoutTest.getKey()));
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
}