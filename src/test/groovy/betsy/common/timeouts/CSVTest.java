package betsy.common.timeouts;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.calibration.CalibrationTimeout;
import betsy.common.timeouts.timeout.Timeout;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public  class CSVTest {
    private Timeout timeout;
    private Path csv;
    private Path properties;
    private CSVTest.TestAppender testAppender;
    private static final Logger LOGGER = Logger.getLogger(CSV.class);


    @BeforeClass
    public static void setUpClass() {
        String transitionFolder = "transition_folder";
        FileTasks.mkdirs(Paths.get(transitionFolder));
        File csv = new File("calibration_timeouts.csv");
        if (csv.exists()) {
            FileTasks.copyFileIntoFolder(csv.toPath(), Paths.get(transitionFolder));
            FileTasks.deleteFile(csv.toPath());
        }
    }

    @AfterClass
    public static void tearDownClass() {
        File csv = new File("calibration_timeouts.csv");
        FileTasks.deleteFile(csv.toPath());
        String transitionFolder = "transition_folder";
        Path currentRelativePath = Paths.get("");
        FileTasks.copyFilesInFolderIntoOtherFolder(Paths.get(transitionFolder), currentRelativePath);
        FileTasks.deleteDirectory(Paths.get(transitionFolder));
    }


    @Before
    public void setUp() throws Exception {
        timeout = new Timeout("ode", "deploy", 20000, 2000);
        properties = Paths.get("timeouts.properties");
        csv = Paths.get("calibration_timeouts.csv");
        testAppender = new TestAppender();
        LOGGER.addAppender(testAppender);
    }

    @After
    public void tearDown() throws Exception {
        timeout = null;
        Files.deleteIfExists(properties);
        Files.deleteIfExists(csv);
        properties = null;
        csv = null;
        LOGGER.removeAllAppenders();
        testAppender = null;
    }

    @Test
    public void testWriteToCSV() throws Exception {
        ArrayList<CalibrationTimeout> calibrationTimeouts = new ArrayList<>();
        calibrationTimeouts.add(new CalibrationTimeout(timeout));
        CSV.write(csv, calibrationTimeouts);
    }

    @Test
    public void testWriteToCSVDoesNotExits() throws Exception {
        FileTasks.deleteFile(csv);
        ArrayList<CalibrationTimeout> calibrationTimeouts = new ArrayList<>();
        calibrationTimeouts.add(new CalibrationTimeout(timeout));
        CSV.write(csv, calibrationTimeouts);
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