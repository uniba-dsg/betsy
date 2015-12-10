package timeouts.timeout;

import org.junit.Test;
import timeouts.calibration_timeout.CalibrationTimeout;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutRepositoryTest {

    @Test
    public void testGetTimeout() throws Exception {
        TimeoutRepository.getTimeout("ode.deploy");
        assertNotNull(TimeoutRepository.getTimeout("ode.deploy"));
    }

    @Test
    public void testSetTimeout() throws Exception {
        Timeout timeout = new Timeout("ode_v", "deploymentTimeout", 20_000, 5_000);
        TimeoutRepository.setTimeout(timeout);
        assertEquals(timeout.getTimeoutInMs(), TimeoutRepository.getTimeout(timeout.getKey()).get().getTimeoutInMs());
        assertEquals(timeout.getTimeToRepetitionInMs(), TimeoutRepository.getTimeout(timeout.getKey()).get().getTimeToRepetitionInMs());
    }

    @Test
    public void testSetAllCalibrationTimeouts() throws Exception {
        CalibrationTimeout timeout = new CalibrationTimeout("ode_v", "deploymentTimeout", "", 20_000, 5_000);
        CalibrationTimeout timeoutTest = new CalibrationTimeout("bpelg_v", "deploymentTimeout", "", 20_000, 5_000);
        HashMap<String, CalibrationTimeout> timeouts = new HashMap<>();
        timeouts.put(timeout.getKey(), timeout);
        timeouts.put(timeout.getKey(), timeoutTest);
        TimeoutRepository.setAllCalibrationTimeouts(timeouts);
        assertEquals(timeout.getTimeoutInMs(), TimeoutRepository.getTimeout(timeout.getKey()).get().getTimeoutInMs());
        assertEquals(timeout.getTimeToRepetitionInMs(), TimeoutRepository.getTimeout(timeout.getKey()).get().getTimeToRepetitionInMs());
        assertEquals(timeoutTest.getTimeoutInMs(), TimeoutRepository.getTimeout(timeoutTest.getKey()).get().getTimeoutInMs());
        assertEquals(timeoutTest.getTimeToRepetitionInMs(), TimeoutRepository.getTimeout(timeoutTest.getKey()).get().getTimeToRepetitionInMs());
    }

    @Test
    public void testWriteToCSV() throws Exception {
        TimeoutRepository.writeToCSV();
        File csv = new File("timeouts.csv");
        assertTrue(csv.exists());
        csv.delete();
    }
}