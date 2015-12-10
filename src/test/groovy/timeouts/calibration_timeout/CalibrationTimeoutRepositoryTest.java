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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class CalibrationTimeoutRepositoryTest {

    private CalibrationTimeout calibrationTimeout;
    private CalibrationTimeout secondCalibrationTimeout;
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(CalibrationTimeouts.class);
    private TestAppender testAppender;

    @Before
    public void setUp() throws Exception {
        testAppender = new TestAppender();
        LOGGER.addAppender(testAppender);
        calibrationTimeout = new CalibrationTimeout("ode", "deploy", "install", 50_000, 5_000);
        secondCalibrationTimeout = new CalibrationTimeout("tomcat", "startup", "install", 50_000, 5_000);
    }

    @After
    public void tearDown() throws Exception {
        LOGGER.removeAllAppenders();
        testAppender = null;
        calibrationTimeout = null;
        secondCalibrationTimeout = null;
        CalibrationTimeoutRepository.clean();
    }

    @Test
    public void testGetAllCalibrationTimeouts() throws Exception {
        CalibrationTimeoutRepository.addTimeout(calibrationTimeout);
        CalibrationTimeoutRepository.addTimeout(secondCalibrationTimeout);
        HashMap<String, CalibrationTimeout> calibrationTimeoutList = CalibrationTimeoutRepository.getAllCalibrationTimeouts();
        assertEquals(calibrationTimeout.getKey(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getKey());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeToRepetitionInMs());
        assertEquals(secondCalibrationTimeout.getKey(), calibrationTimeoutList.get(secondCalibrationTimeout.getKey()).getKey());
        assertEquals(secondCalibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(secondCalibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals(secondCalibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(secondCalibrationTimeout.getKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testAddTimeout() throws Exception {
        CalibrationTimeoutRepository.addTimeout(calibrationTimeout);
        HashMap<String, CalibrationTimeout> calibrationTimeoutList = CalibrationTimeoutRepository.getAllCalibrationTimeouts();
        assertEquals(calibrationTimeout.getKey(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getKey());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testAddTimeoutExisting() throws Exception {
        CalibrationTimeoutRepository.addTimeout(calibrationTimeout);
        CalibrationTimeoutRepository.addTimeout(calibrationTimeout);
        assertEquals("The timeout, which should be added to the CalibrationTimeouts, is already existing.", testAppender.messages.get(0));
    }

    @Test
    public void testSetCalibrationTimeout() throws Exception {
        CalibrationTimeoutRepository.addTimeout(calibrationTimeout);
        calibrationTimeout.setValue(2_000);
        calibrationTimeout.setTimeToRepetition(5_000);
        calibrationTimeout.setStatus(CalibrationTimeout.Status.TOO_HIGH);
        CalibrationTimeoutRepository.setCalibrationTimeout(calibrationTimeout);
        HashMap<String, CalibrationTimeout> calibrationTimeoutList = CalibrationTimeoutRepository.getAllCalibrationTimeouts();
        assertEquals(calibrationTimeout.getKey(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getKey());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testSetCalibrationTimeoutNotExisting() throws Exception {
        CalibrationTimeoutRepository.setCalibrationTimeout(calibrationTimeout);
        HashMap<String, CalibrationTimeout> calibrationTimeoutList = CalibrationTimeoutRepository.getAllCalibrationTimeouts();
        assertEquals(calibrationTimeout.getKey(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getKey());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testSetAllCalibrationTimeouts() throws Exception {
        HashMap<String, CalibrationTimeout> calibrationTimeoutHashMap = new HashMap<>();
        calibrationTimeoutHashMap.put(calibrationTimeout.getKey(), calibrationTimeout);
        CalibrationTimeoutRepository.addTimeout(calibrationTimeout);

        calibrationTimeoutHashMap.put(secondCalibrationTimeout.getKey(), secondCalibrationTimeout);
        CalibrationTimeoutRepository.addTimeout(secondCalibrationTimeout);

        CalibrationTimeoutRepository.setAllCalibrationTimeouts(calibrationTimeoutHashMap);
        HashMap<String, CalibrationTimeout> calibrationTimeoutList = CalibrationTimeoutRepository.getAllCalibrationTimeouts();
        assertEquals(calibrationTimeout.getKey(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getKey());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testSetAllCalibrationTimeoutsNotExisting() throws Exception {
        HashMap<String, CalibrationTimeout> calibrationTimeoutHashMap = new HashMap<>();
        calibrationTimeoutHashMap.put(calibrationTimeout.getKey(), calibrationTimeout);
        calibrationTimeoutHashMap.put(secondCalibrationTimeout.getKey(), secondCalibrationTimeout);
        CalibrationTimeoutRepository.setAllCalibrationTimeouts(calibrationTimeoutHashMap);

        HashMap<String, CalibrationTimeout> calibrationTimeoutList = CalibrationTimeoutRepository.getAllCalibrationTimeouts();
        assertEquals(calibrationTimeout.getKey(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getKey());
        assertEquals(calibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals(calibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(calibrationTimeout.getKey()).getTimeToRepetitionInMs());
        assertEquals(secondCalibrationTimeout.getKey(), calibrationTimeoutList.get(secondCalibrationTimeout.getKey()).getKey());
        assertEquals(secondCalibrationTimeout.getTimeoutInMs(), calibrationTimeoutList.get(secondCalibrationTimeout.getKey()).getTimeoutInMs());
        assertEquals(secondCalibrationTimeout.getTimeToRepetitionInMs(), calibrationTimeoutList.get(secondCalibrationTimeout.getKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testWriteAllCalibrationTimeoutsToProperties() throws Exception {
        File f = new File("timeout.properties");
        if (!f.exists()) {
            HashMap<String, CalibrationTimeout> calibrationTimeoutHashMap = new HashMap<>();
            calibrationTimeoutHashMap.put(calibrationTimeout.getKey(), calibrationTimeout);
            CalibrationTimeoutRepository.setAllCalibrationTimeouts(calibrationTimeoutHashMap);
            CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();
        }
        assertTrue(f.exists());
    }

    @Test
    public void testWriteToCSV() throws Exception {
        File f = new File("calibration_timeouts.csv");
        if (!f.exists()) {
            HashMap<String, CalibrationTimeout> calibrationTimeoutHashMap = new HashMap<>();
            calibrationTimeoutHashMap.put(calibrationTimeout.getKey(), calibrationTimeout);
            CalibrationTimeoutRepository.setAllCalibrationTimeouts(calibrationTimeoutHashMap);
            CalibrationTimeoutRepository.writeToCSV();
        }
        assertTrue(f.exists());
    }

    @Test
    public void testClean() throws Exception {
        CalibrationTimeoutRepository.addTimeout(calibrationTimeout);
        CalibrationTimeoutRepository.clean();
        assertEquals(0, CalibrationTimeoutRepository.getAllCalibrationTimeouts().size());
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