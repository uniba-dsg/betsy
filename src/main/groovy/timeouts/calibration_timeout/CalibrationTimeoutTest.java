package timeouts.calibration_timeout;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import timeouts.timeout.Timeout;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class CalibrationTimeoutTest {

    private CalibrationTimeout calibrationTimeout;

    @Before
    public void setUp() throws Exception {
        calibrationTimeout = new CalibrationTimeout(new Timeout("ode", "deploy", "maven", 30_000, 1200));
    }

    @After
    public void tearDown() throws Exception {
        calibrationTimeout = null;
    }

    @Test
    public void testConstructorWithTimeout(){
        assertEquals("ode", calibrationTimeout.getEngineOrProcessGroup());
        assertEquals("deploy", calibrationTimeout.getStepOrProcess());
        assertEquals("ode.deploy.maven", calibrationTimeout.getKey());
        assertEquals("maven", calibrationTimeout.getDescription());
        assertEquals(30_000, calibrationTimeout.getTimeoutInMs());
        assertEquals(new BigDecimal(30), calibrationTimeout.getTimeoutInSeconds());
        assertEquals(new BigDecimal(0.5), calibrationTimeout.getTimeoutInMinutes());
        assertEquals(1200, calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals(new BigDecimal(1.2), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals(new BigDecimal(0.02), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals(CalibrationTimeout.Status.NOT_SET, calibrationTimeout.getStatus());
    }

    @Test
    public void testConstructor(){
        calibrationTimeout = new CalibrationTimeout("ode", "deploy", "maven", 30_000, 1200);
        assertEquals("ode", calibrationTimeout.getEngineOrProcessGroup());
        assertEquals("deploy", calibrationTimeout.getStepOrProcess());
        assertEquals("ode.deploy.maven", calibrationTimeout.getKey());
        assertEquals("maven", calibrationTimeout.getDescription());
        assertEquals(30_000, calibrationTimeout.getTimeoutInMs());
        assertEquals(new BigDecimal(30), calibrationTimeout.getTimeoutInSeconds());
        assertEquals(new BigDecimal(0.5), calibrationTimeout.getTimeoutInMinutes());
        assertEquals(1200, calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals(new BigDecimal(1.2), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals(new BigDecimal(0.02), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals(CalibrationTimeout.Status.NOT_SET, calibrationTimeout.getStatus());
    }

    @Test
    public void testGetStatusAfterCreation() throws Exception {
        assertEquals(CalibrationTimeout.Status.NOT_SET, calibrationTimeout.getStatus());
    }

    @Test
    public void testSetStatus() throws Exception {
        CalibrationTimeout.Status status = CalibrationTimeout.Status.TOO_HIGH;
        calibrationTimeout.setStatus(status);
        assertEquals(status, calibrationTimeout.getStatus());
    }

    @Test
    public void testGetStatus() throws Exception {
        CalibrationTimeout.Status status = CalibrationTimeout.Status.TOO_HIGH;
        calibrationTimeout.setStatus(status);
        assertEquals(status, calibrationTimeout.getStatus());
    }
}