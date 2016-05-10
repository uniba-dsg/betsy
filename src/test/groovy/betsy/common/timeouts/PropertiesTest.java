package betsy.common.timeouts;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.timeout.Timeout;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.*;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class PropertiesTest {
    private Timeout timeout;
    private Path properties;
    private PropertiesTest.TestAppender testAppender;
    private static final Logger LOGGER = Logger.getLogger(Properties.class);


    @BeforeClass
    public static void setUpClass() {
        String transitionFolder = "transition_folder";
        FileTasks.mkdirs(Paths.get(transitionFolder));
        File properties = new File("timeout.properties");
        if (properties.exists()) {
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
        FileTasks.copyFilesInFolderIntoOtherFolder(Paths.get(transitionFolder), currentRelativePath);
        FileTasks.deleteDirectory(Paths.get(transitionFolder));
    }


    @Before
    public void setUp() throws Exception {
        timeout = new Timeout("ode", "deploy", 20000, 2000);
        properties = Paths.get("timeouts.properties");
        testAppender = new TestAppender();
        LOGGER.addAppender(testAppender);
    }

    @After
    public void tearDown() throws Exception {
        timeout = null;
        Files.deleteIfExists(properties);
        properties = null;
        LOGGER.removeAllAppenders();
        testAppender = null;
    }

    @Test
    public void testWriteToProperties() throws Exception {
        ArrayList<Timeout> timeouts = new ArrayList<>();
        timeouts.add(timeout);

        Properties.read(properties, timeouts);
        List<Timeout> readTimeouts = Properties.read(properties, timeouts);
        for (Timeout actualTimeout : readTimeouts) {
            assertEquals(timeout.getKey(), actualTimeout.getKey());
            assertEquals(timeout.getTimeoutInMs(), actualTimeout.getTimeoutInMs());
            assertEquals(timeout.getTimeToRepetitionInMs(), actualTimeout.getTimeToRepetitionInMs());
        }
    }

    @Test
    public void testWriteToPropertiesExtendedValues() throws Exception {
        ArrayList<Timeout> timeouts = new ArrayList<>();
        timeouts.add(timeout);
        Timeout testTimeout = new Timeout("tomcat", "start", 20000, 2000);

        Properties.write(properties, timeouts);
        List<Timeout> readTimeouts = Properties.read(properties, timeouts);
        for (Timeout actualTimeout : readTimeouts) {
            assertEquals(timeout.getKey(), actualTimeout.getKey());
            assertEquals(timeout.getTimeoutInMs(), actualTimeout.getTimeoutInMs());
            assertEquals(timeout.getTimeToRepetitionInMs(), actualTimeout.getTimeToRepetitionInMs());
        }
        timeouts.remove(timeout);
        timeout.setValue(5000);
        timeout.setTimeToRepetition(500);
        timeouts.add(testTimeout);
        Properties.write(properties, timeouts);
        timeouts.add(timeout);

        List<Timeout> readNewTimeouts = Properties.read(properties, timeouts);
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
        ArrayList<Timeout> timeouts = new ArrayList<>();
        timeouts.add(timeout);

        List<Timeout> readTimeouts = Properties.read(properties, timeouts);
        for (Timeout actualTimeout : readTimeouts) {
            assertEquals(timeout.getKey(), actualTimeout.getKey());
            assertEquals(timeout.getTimeoutInMs(), actualTimeout.getTimeoutInMs());
            assertEquals(timeout.getTimeToRepetitionInMs(), actualTimeout.getTimeToRepetitionInMs());
        }
    }

    @Test
    public void testReadFromPropertiesReadable() throws Exception {
        ArrayList<Timeout> timeouts = new ArrayList<>();
        timeouts.add(timeout);

        properties.toFile().setReadable(false);
        Properties.read(properties, timeouts);
        assertEquals("The file " + properties.toString() + " is not readable.", testAppender.messages.get(0));
    }

    @Test
    public void testReadFromPropertiesTimeoutNumberFormat() throws Exception {
        ArrayList<Timeout> timeouts = new ArrayList<>();
        timeouts.add(timeout);

        java.util.Properties timeoutProperties = System.getProperties();
        FileWriter writer = new FileWriter(properties.toString());
        timeoutProperties.setProperty(timeout.getKey() + ".value", "test");
        timeoutProperties.setProperty(timeout.getKey() + ".timeToRepetition", "test");
        timeoutProperties.store(writer, "Timeout_properties");
        writer.close();

        Properties.read(properties, timeouts);
        assertEquals("The timeout with the key " + timeout.getKey() + " was not read from the properties. The timeout have to be an integer.", testAppender.messages.get(0));
        assertEquals("The timeToRepetition with the key " + timeout.getKey() + " was not read from the properties. The timeToRepetition have to be an integer.", testAppender.messages.get(1));
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