package betsy.common.timeouts.timeout;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.calibration.CalibrationTimeout;
import betsy.common.timeouts.calibration.CalibrationTimeoutRepository;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutsTest {

    private File properties;
    private String engine;
    private String step;
    private Integer value;



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
        engine = "Ode";
        step = "deploy";
        value = 20_000;
    }

    @After
    public void tearDown() throws Exception {
        engine = null;
        step = null;
        value = null;
        Files.deleteIfExists(properties.toPath());
        properties = null;
    }

    @Test
    public void testConstructor() throws Exception {
        Timeouts timeouts = new Timeouts();
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals("The number should conform with the number of timeouts in method 'addtimeouts'", 54, timeoutHashMap.size());
        assertEquals("The keys should be equal.", "Tomcat.startup", timeoutHashMap.get("Tomcat.startup").getKey());
    }

    @Test
    public void testConstructorWithOwnTimeouts() throws Exception {
        StringBuilder key = new StringBuilder().append(engine).append(".").append(step);
        Timeout timeout = new Timeout(engine, step, value);
        ArrayList<Timeout> timeoutList = new ArrayList<>();
        timeoutList.add(timeout);
        Timeouts timeouts = new Timeouts(timeoutList);
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals("The sizes should be equal.", timeoutList.size(), timeoutHashMap.size());
        assertEquals("The keys should be equal.", key.toString(), timeoutHashMap.get(key.toString()).getKey());
    }

    @Test
    public void testConstructorWithOwnProperties() throws Exception {
        FileTasks.deleteFile(properties.toPath());
        Timeout timeout = new Timeout(engine, step, value);
        CalibrationTimeoutRepository.addCalibrationTimeout(new CalibrationTimeout(timeout));
        CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();

        Timeouts timeouts = new Timeouts(properties.getName());
        timeouts.readTimeoutProperties();
        assertEquals("The keys should be equal.", timeout.getKey(), timeouts.getTimeout(timeout.getKey()).getKey());
        assertEquals("The timeouts in ms should be equal.", timeout.getTimeoutInMs(), timeouts.getTimeout(timeout.getKey()).getTimeoutInMs());
    }

    @Test
    public void testConstructorWithOwnPropertiesTimeouts() throws Exception {
        FileTasks.deleteFile(properties.toPath());
        Integer timeToRepetition = 500;
        Timeout timeout = new Timeout(engine, step, value, timeToRepetition);
        CalibrationTimeoutRepository.addCalibrationTimeout(new CalibrationTimeout(timeout));
        CalibrationTimeoutRepository.writeAllCalibrationTimeoutsToProperties();

        Timeout timeoutTest = new Timeout(engine, step, value, timeToRepetition);
        ArrayList<Timeout> timeoutList = new ArrayList<>();
        timeoutList.add(timeoutTest);

        Timeouts timeouts = new Timeouts(timeoutList, properties.getName());
        timeouts.readTimeoutProperties();
        assertEquals("The keys should be equal.", timeoutTest.getKey(), timeouts.getTimeout(timeoutTest.getKey()).getKey());
        assertEquals("The timeouts in ms should be equal.", timeoutTest.getTimeoutInMs(), timeouts.getTimeout(timeoutTest.getKey()).getTimeoutInMs());
    }


    @Test
    public void testGetHashMap() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        ArrayList<Timeout> timeoutList = new ArrayList<>();
        timeoutList.add(timeout);

        Timeouts timeouts = new Timeouts(timeoutList);
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals("The sizes should be equal.", timeoutList.size(), timeoutHashMap.size());
        assertEquals("The timeouts should be equal.", timeout, timeoutHashMap.get(engine + "." + step));
    }

    @Test
    public void testGetTimeout() throws Exception {
        StringBuilder key = new StringBuilder().append(engine).append(".").append(step);

        Timeout timeout = new Timeout(engine, step, value);
        ArrayList<Timeout> timeoutList = new ArrayList<>();
        timeoutList.add(timeout);

        Timeouts timeouts = new Timeouts(timeoutList);
        Timeout timeoutTest = timeouts.getTimeout(key.toString());
        assertEquals("The keys should be equal.", key.toString(), timeoutTest.getKey());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetTimeoutNotExists() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        ArrayList<Timeout> timeoutList = new ArrayList<>();
        timeoutList.add(timeout);

        Timeouts timeouts = new Timeouts(timeoutList);
        timeouts.getTimeout("glassfish.deploy");
    }

    @Test
    public void testGetAllTimeouts() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        ArrayList<Timeout> timeoutList = new ArrayList<>();
        timeoutList.add(timeout);

        Timeouts timeouts = new Timeouts(timeoutList);
        HashMap<String, Timeout> timeoutHashMap = timeouts.getAllTimeouts();
        assertEquals("The sizes should be equal.", timeoutList.size(), timeoutHashMap.size());
    }

    @Test
    public void testSetTimeout() throws Exception {
        Integer timeToRepetition = 500;
        Timeout timeout = new Timeout(engine, step, value, timeToRepetition);
        ArrayList<Timeout> timeoutList = new ArrayList<>();
        timeoutList.add(timeout);

        value = 30_000;
        timeToRepetition = 5_000;
        timeout.setValue(value);
        timeout.setTimeToRepetition(timeToRepetition);
        Timeouts timeouts = new Timeouts(timeoutList);
        timeouts.setTimeout(timeout);
        Timeout testTimeout = timeouts.getTimeout(timeout.getKey());
        assertEquals("The values in ms should be equal.", value, testTimeout.getTimeoutInMs(), 0);
        assertEquals("The timeToRepetitions should be equal.", timeToRepetition, testTimeout.getTimeToRepetitionInMs(), 0);
    }

    @Test(expected = NoSuchElementException.class)
    public void testSetTimeoutNotExists() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        ArrayList<Timeout> timeoutList = new ArrayList<>();
        timeoutList.add(timeout);

        Timeouts timeouts = new Timeouts(timeoutList);
        Timeout timeoutTest = new Timeout("glassfish", "deploy", 20_000, 3_000);
        timeouts.setTimeout(timeoutTest);
    }
}
