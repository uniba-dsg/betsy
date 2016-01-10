package betsy.common.timeouts;

import betsy.common.tasks.FileTasks;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.*;
import betsy.common.timeouts.calibration_timeout.CalibrationTimeout;
import betsy.common.timeouts.timeout.Timeout;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutIOOperationsTest {

    private ArrayList<Timeout> timeouts;
    private Timeout timeout;
    private Timeout testTimeout;
    private File csv;
    private File properties;
    private TestAppender testAppender;
    private static final Logger LOGGER = Logger.getLogger(TimeoutIOOperations.class);


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
        timeouts = new ArrayList<>();
        timeout = new Timeout("ode", "deploy", 20000, 2000);
        testTimeout = new Timeout("tomcat", "start", 20000, 2000);
        timeouts.add(timeout);
        properties = new File("timeouts.properties");
        csv = new File("calibration_timeouts.csv");
        testAppender = new TestAppender();
        LOGGER.addAppender(testAppender);
    }

    @After
    public void tearDown() throws Exception {
        timeouts = null;
        timeout = null;
        testTimeout = null;
        Files.deleteIfExists(properties.toPath());
        Files.deleteIfExists(csv.toPath());
        properties = null;
        csv = null;
        LOGGER.removeAllAppenders();
        testAppender = null;
    }

    @Test
    public void testWriteToProperties() throws Exception {
        TimeoutIOOperations.writeToProperties(properties, timeouts);
        List<Timeout> readTimeouts = TimeoutIOOperations.readFromProperties(properties, timeouts);
        for (Timeout actualTimeout : readTimeouts) {
            assertEquals(timeout.getKey(), actualTimeout.getKey());
            assertEquals(timeout.getTimeoutInMs(), actualTimeout.getTimeoutInMs());
            assertEquals(timeout.getTimeToRepetitionInMs(), actualTimeout.getTimeToRepetitionInMs());
        }
    }

    @Test
    public void testWriteToPropertiesTimeoutNull() throws Exception {
        TimeoutIOOperations.readFromProperties(properties, null);
        assertEquals("The property file or the timeouts were null.", testAppender.messages.get(0));
    }

    @Test
    public void testWriteToPropertiesPropertiesNull() throws Exception {
        TimeoutIOOperations.readFromProperties(null, timeouts);
        assertEquals("The property file or the timeouts were null.", testAppender.messages.get(0));
    }

    @Test
    public void testWriteToPropertiesExtendedValues() throws Exception {
        TimeoutIOOperations.writeToProperties(properties, timeouts);
        List<Timeout> readTimeouts = TimeoutIOOperations.readFromProperties(properties, timeouts);
        for (Timeout actualTimeout : readTimeouts) {
            assertEquals(timeout.getKey(), actualTimeout.getKey());
            assertEquals(timeout.getTimeoutInMs(), actualTimeout.getTimeoutInMs());
            assertEquals(timeout.getTimeToRepetitionInMs(), actualTimeout.getTimeToRepetitionInMs());
        }
        timeouts.remove(timeout);
        timeout.setValue(5000);
        timeout.setTimeToRepetition(500);
        timeouts.add(testTimeout);
        TimeoutIOOperations.writeToProperties(properties, timeouts);
        timeouts.add(timeout);

        List<Timeout> readNewTimeouts = TimeoutIOOperations.readFromProperties(properties, timeouts);
        for (Timeout actualTimeout : readNewTimeouts) {
            if (Objects.equals(actualTimeout.getKey(), timeout.getKey())) {
                assertEquals(timeout.getKey(), actualTimeout.getKey());
                assertEquals(timeout.getTimeoutInMs(), actualTimeout.getTimeoutInMs());
                assertEquals(timeout.getTimeToRepetitionInMs(), actualTimeout.getTimeToRepetitionInMs());
            } else {
                assertEquals(testTimeout.getKey(), actualTimeout.getKey());
                assertEquals(testTimeout.getTimeoutInMs(), actualTimeout.getTimeoutInMs());
                assertEquals(testTimeout.getTimeToRepetitionInMs(), actualTimeout.getTimeToRepetitionInMs());
            }
        }
    }

    @Test
    public void testReadFromProperties() throws Exception {
        List<Timeout> readTimeouts = TimeoutIOOperations.readFromProperties(properties, timeouts);
        for (Timeout actualTimeout : readTimeouts) {
            assertEquals(timeout.getKey(), actualTimeout.getKey());
            assertEquals(timeout.getTimeoutInMs(), actualTimeout.getTimeoutInMs());
            assertEquals(timeout.getTimeToRepetitionInMs(), actualTimeout.getTimeToRepetitionInMs());
        }
    }

    @Test
    public void testReadFromPropertiesPropertiesNull() throws Exception {
        TimeoutIOOperations.readFromProperties(null, timeouts);
        assertEquals("The property file or the timeouts were null.", testAppender.messages.get(0));
    }

    @Test
    public void testReadFromPropertiesTimeoutsNull() throws Exception {
        TimeoutIOOperations.readFromProperties(properties, null);
        assertEquals("The property file or the timeouts were null.", testAppender.messages.get(0));
    }

    @Test
    public void testReadFromPropertiesReadable() throws Exception {
        properties.setReadable(false);
        TimeoutIOOperations.readFromProperties(properties, timeouts);
        assertEquals("The file " + properties.getName() + " is not readable.", testAppender.messages.get(0));
    }

    @Test
    public void testReadFromPropertiesTimeoutNumberFormat() throws Exception {
        Properties timeoutProperties = System.getProperties();
        FileWriter writer = new FileWriter(properties);
        timeoutProperties.setProperty(timeout.getKey() + ".value", "test");
        timeoutProperties.setProperty(timeout.getKey() + ".timeToRepetition", "test");
        timeoutProperties.store(writer, "Timeout_properties");
        writer.close();

        TimeoutIOOperations.readFromProperties(properties, timeouts);
        assertEquals("The timeout with the key " + timeout.getKey() + " was not read from the properties. The timeout have to be an integer.", testAppender.messages.get(0));
        assertEquals("The timeToRepetition with the key " + timeout.getKey() + " was not read from the properties. The timeToRepetition have to be an integer.", testAppender.messages.get(1));
    }


    @Test
    public void testWriteToCSV() throws Exception {
        ArrayList<CalibrationTimeout> calibrationTimeouts = new ArrayList<>();
        calibrationTimeouts.add(new CalibrationTimeout(timeout));
        TimeoutIOOperations.writeToCSV(csv, calibrationTimeouts);
    }

    @Test
    public void testWriteToCSVNullCSV() throws Exception {
        ArrayList<CalibrationTimeout> calibrationTimeouts = new ArrayList<>();
        calibrationTimeouts.add(new CalibrationTimeout(timeout));
        TimeoutIOOperations.writeToCSV(null, calibrationTimeouts);
        assertEquals("The csv file or the timeouts were null.", testAppender.messages.get(0));
    }

    @Test
    public void testWriteToCSVDoesNotExits() throws Exception {
        FileTasks.deleteFile(csv.toPath());
        ArrayList<CalibrationTimeout> calibrationTimeouts = new ArrayList<>();
        calibrationTimeouts.add(new CalibrationTimeout(timeout));
        TimeoutIOOperations.writeToCSV(csv, calibrationTimeouts);
    }

    @Test
    public void testWriteToCSVNullTimeouts() throws Exception {
        TimeoutIOOperations.writeToCSV(csv, null);
        assertEquals("The csv file or the timeouts were null.", testAppender.messages.get(0));
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
