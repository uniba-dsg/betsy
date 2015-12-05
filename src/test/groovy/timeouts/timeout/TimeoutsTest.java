package timeouts.timeout;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import timeouts.TimeoutIOOperations;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutsTest {

    private File properties;
    private StringBuilder key;
    private Integer value;
    private Integer timeToRepetition;
    private Timeout timeout;
    ArrayList<Timeout> timeoutList;
    private String csv;
    private Timeouts timeouts;
    private TestAppender testAppender;
    private TestAppender testAppenderOperations;
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(Timeouts.class);
    private static final Logger LOGGER_OPERATIONS = org.apache.log4j.Logger.getLogger(TimeoutIOOperations.class);

    @Before
    public void setUp() throws Exception {
        properties = new File("test_timeout.properties");
        String engine = "ode";
        String step = "step";
        key = new StringBuilder().append(engine).append(".").append(step);
        value = 20_000;
        timeToRepetition = 500;
        timeout = new Timeout(engine, step, value, timeToRepetition);
        timeoutList = new ArrayList<>();
        timeoutList.add(timeout);
        csv = "test_csv";
        timeouts = new Timeouts(timeoutList, properties.toString(), csv);
        testAppender = new TestAppender();
        testAppenderOperations = new TestAppender();
        LOGGER.addAppender(testAppender);
        LOGGER_OPERATIONS.addAppender(testAppenderOperations);

    }

    @After
    public void tearDown() throws Exception {
        key = null;
        value = null;
        timeToRepetition = null;
        timeout = null;
        timeouts = null;
        testAppender = null;
        LOGGER.addAppender(null);
        Files.deleteIfExists(properties.toPath());
        Files.deleteIfExists(new File(csv + ".csv").toPath());
        csv = null;
        properties = null;
    }

    @Test
    public void testConstructor() throws Exception {
        timeouts = new Timeouts();
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals(94, timeoutHashMap.size());
        assertEquals("Tomcat.startup", timeoutHashMap.get("Tomcat.startup").getKey());
    }

    @Test
    public void testConstructorWithOwnTimeOutsPropertiesAndCSV() throws Exception {
        timeouts = new Timeouts(timeoutList, properties.toString(), csv);
        timeouts.writeAllTimeoutsToProperties();
        timeouts.writeToCSV();
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals(1, timeoutHashMap.size());
        assertEquals(key.toString(), timeoutHashMap.get(key.toString()).getKey());
        assertEquals(true, properties.exists());
        assertEquals(true, new File(csv + ".csv").exists());
    }

    @Test
    public void testConstructorWithOwnPropertiesAndCSV() throws Exception {
        timeouts = new Timeouts(properties.toString(), csv);
        timeouts.writeAllTimeoutsToProperties();
        timeouts.writeToCSV();
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals(94, timeoutHashMap.size());
        assertEquals("Tomcat.startup", timeoutHashMap.get("Tomcat.startup").getKey());
        assertEquals(true, properties.exists());
        assertEquals(true, new File(csv + ".csv").exists());
    }

    @Test
    public void testConstructorWithOwnTimeouts() throws Exception {
        timeouts = new Timeouts(timeoutList);
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals(timeoutList.size(), timeoutHashMap.size());
        assertEquals(key.toString(), timeoutHashMap.get(key.toString()).getKey());
    }

    @Test
    public void testGetHashMap() throws Exception {
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals(timeoutList.size(), timeoutHashMap.size());
        assertEquals(timeout, timeoutHashMap.get(key.toString()));
    }

    @Test
    public void testGetTimeout() throws Exception {
        Optional<Timeout> timeout = timeouts.getTimeout(key.toString());
        assertEquals(key.toString(), timeout.get().getKey());
    }

    @Test
    public void testGetTimeoutNotExists() throws Exception {
        Optional<Timeout> timeout = timeouts.getTimeout("glassfish.deploy");
        assertEquals(false, timeout.isPresent());
    }

    @Test
    public void testGetAllTimeouts() throws Exception {
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals(timeoutList.size(), timeoutHashMap.size());
    }

    @Test
    public void testSetTimeout() throws Exception {
        value = 30_000;
        timeToRepetition = 5_000;
        timeout.setValue(value);
        timeout.setTimeToRepetition(timeToRepetition);
        timeouts.setTimeout(timeout);
        Timeout testTimeout = timeouts.getTimeout(timeout.getKey()).get();
        assertEquals(value, testTimeout.getTimeoutInMs(), 0);
        assertEquals(timeToRepetition, testTimeout.getTimeToRepetitionInMs(), 0);
    }

    @Test
    public void testSetTimeoutNotExists() throws Exception {
        Timeout timeout = new Timeout("glassfish", "deploy", 20_000, 3_000);
        timeouts.setTimeout(timeout);
        assertEquals("The timeout(" + timeout.getKey() + ") does not exist.", testAppender.messages.get(0));
    }

    @Test
    public void testWriteAllTimeoutsToProperties() throws Exception {
        timeouts.writeAllTimeoutsToProperties();
        assertTrue(properties.exists());
    }

    @Test
    public void testReadTimeoutProperties() throws Exception {
        timeouts.writeAllTimeoutsToProperties();
        timeouts.readTimeoutProperties();
        timeout = timeouts.getTimeout(key.toString()).get();
        assertEquals(value, timeout.getTimeoutInMs(), 0);
        assertEquals(timeToRepetition, timeout.getTimeToRepetitionInMs(), 0);
    }

    @Test
    public void testReadTimeoutPropertiesFileDoesNotExist() throws Exception {
        Files.deleteIfExists(properties.toPath());
        timeouts.readTimeoutProperties();
        assertEquals("The file " + properties + " is not readable.", testAppenderOperations.messages.get(0));
    }

    @Test
    public void testReadTimeoutPropertiesNumberFormatValue() throws Exception {
        Writer writer = new FileWriter(properties);
        Properties properties = new Properties(System.getProperties());
        properties.setProperty(timeout.getKey() + ".value", "test");
        properties.setProperty(timeout.getKey() + ".timeToRepetition", "test");
        properties.store(writer, "Timeout_properties");
        timeouts.readTimeoutProperties();
        writer.close();
        assertEquals("The timeout with the key " + key.toString() + " was not read from the properties. The timeout have to be an integer.", testAppenderOperations.messages.get(0));
        assertEquals("The timeToRepetition with the key " + key.toString() + " was not read from the properties. The timeToRepetition have to be an integer.", testAppenderOperations.messages.get(1));
    }

    @Test
    public void testWriteToCSV() throws Exception {
        timeouts.writeToCSV();
        File file = new File(csv + ".csv");
        assertTrue(file.exists());
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