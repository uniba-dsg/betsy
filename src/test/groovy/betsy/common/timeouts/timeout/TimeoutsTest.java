package betsy.common.timeouts.timeout;

import betsy.common.tasks.FileTasks;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.*;
import betsy.common.timeouts.calibration.CalibrationTimeout;
import betsy.common.timeouts.calibration.CalibrationTimeoutRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

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
    private Timeouts timeouts;
    private TestAppender testAppender;
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(Timeouts.class);

    @BeforeClass
    public static void setUpClass() {
        String transitionFolder = "transition_folder";
        File properties = new File("timeout.properties");
        if (properties.exists()){
            FileTasks.mkdirs(Paths.get(transitionFolder));
            FileTasks.copyFileIntoFolder(properties.toPath(), Paths.get(transitionFolder));
            FileTasks.deleteFile(properties.toPath());
        }
    }

    @AfterClass
    public static void tearDownClass() {
        File properties = new File("timeout.properties");
        FileTasks.deleteFile(properties.toPath());
        String transitionFolder = "transition_folder";
        Path currentRelativePath = Paths.get("");
        if(Files.exists(Paths.get(transitionFolder))){
            FileTasks.copyFilesInFolderIntoOtherFolder(Paths.get(transitionFolder), currentRelativePath);
            FileTasks.deleteDirectory(Paths.get(transitionFolder));
        }
    }


    @Before
    public void setUp() throws Exception {
        properties = new File("timeout.properties");
        String engine = "OdeDeployer";
        String step = "constructor";
        key = new StringBuilder().append(engine).append(".").append(step);
        value = 20_000;
        timeToRepetition = 500;
        timeout = new Timeout(engine, step, value, timeToRepetition);
        timeoutList = new ArrayList<>();
        timeoutList.add(timeout);
        timeouts = new Timeouts(timeoutList);

        testAppender = new TestAppender();
        LOGGER.addAppender(testAppender);
    }

    @After
    public void tearDown() throws Exception {
        key = null;
        value = null;
        timeToRepetition = null;
        timeout = null;
        timeouts = null;
        LOGGER.removeAllAppenders();
        testAppender = null;
        Files.deleteIfExists(properties.toPath());
        properties = null;
    }

    @Test
    public void testConstructor() throws Exception {
        timeouts = new Timeouts();
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals(54, timeoutHashMap.size());
        assertEquals("Tomcat.startup", timeoutHashMap.get("Tomcat.startup").getKey());
    }

    @Test
    public void testConstructorWithOwnTimeouts() throws Exception {
        timeouts = new Timeouts(timeoutList);
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals(timeoutList.size(), timeoutHashMap.size());
        assertEquals(key.toString(), timeoutHashMap.get(key.toString()).getKey());
    }

    @Test
    public void testConstructorWithOwnProperties() throws Exception {
        FileTasks.deleteFile(properties.toPath());
        CalibrationTimeoutRepository.addCalibrationTimeout(new CalibrationTimeout(timeout));
        CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();

        timeouts = new Timeouts(properties.getName());
        timeouts.readTimeoutProperties();
        assertEquals(timeout.getKey(), timeouts.getTimeout(timeout.getKey()).get().getKey());
        assertEquals(timeout.getTimeoutInMs(), timeouts.getTimeout(timeout.getKey()).get().getTimeoutInMs());
    }

    @Test
    public void testConstructorWithOwnPropertiesTimeouts() throws Exception {
        FileTasks.deleteFile(properties.toPath());
        CalibrationTimeoutRepository.addCalibrationTimeout(new CalibrationTimeout(timeout));
        CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();

        timeouts = new Timeouts(timeoutList, properties.getName());
        timeouts.readTimeoutProperties();
        assertEquals(timeout.getKey(), timeouts.getTimeout(timeout.getKey()).get().getKey());
        assertEquals(timeout.getTimeoutInMs(), timeouts.getTimeout(timeout.getKey()).get().getTimeoutInMs());
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
