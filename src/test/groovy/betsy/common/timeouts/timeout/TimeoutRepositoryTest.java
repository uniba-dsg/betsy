package betsy.common.timeouts.timeout;

import org.junit.Test;
import betsy.common.timeouts.calibration.CalibrationTimeout;

import java.util.HashMap;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutRepositoryTest {

    @Test
    public void testGetTimeout() throws Exception {
        assertNotNull("It isn't null, if the list in the timeouts contains the timeout.", TimeoutRepository.getTimeout("tomcat.startup"));
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetTimeoutNotExists() throws Exception {
        TimeoutRepository.getTimeout("ode.deploy");
    }

    @Test
    public void testSetTimeout() throws Exception {
        Timeout timeout = new Timeout("ode_v", "service", 20_000, 5_000);
        TimeoutRepository.setTimeout(timeout);
        assertEquals("The timeouts in ms should be equal.", timeout.getTimeoutInMs(), TimeoutRepository.getTimeout(timeout.getKey()).getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeout.getTimeToRepetitionInMs(), TimeoutRepository.getTimeout(timeout.getKey()).getTimeToRepetitionInMs());
    }

    @Test
    public void testSetAllCalibrationTimeouts() throws Exception {
        CalibrationTimeout timeout = new CalibrationTimeout("ode_v", "service", "", 20_000, 5_000);
        CalibrationTimeout timeoutTest = new CalibrationTimeout("bpelg_v", "service", "", 20_000, 5_000);
        HashMap<String, CalibrationTimeout> timeouts = new HashMap<>();
        timeouts.put(timeout.getKey(), timeout);
        timeouts.put(timeout.getKey(), timeoutTest);
        TimeoutRepository.setAllCalibrationTimeouts(timeouts);
        assertEquals("The timeouts in ms should be equal.", timeout.getTimeoutInMs(), TimeoutRepository.getTimeout(timeout.getKey()).getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeout.getTimeToRepetitionInMs(), TimeoutRepository.getTimeout(timeout.getKey()).getTimeToRepetitionInMs());
        assertEquals("The timeouts in ms should be equal.", timeoutTest.getTimeoutInMs(), TimeoutRepository.getTimeout(timeoutTest.getKey()).getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeoutTest.getTimeToRepetitionInMs(), TimeoutRepository.getTimeout(timeoutTest.getKey()).getTimeToRepetitionInMs());
    }
}
