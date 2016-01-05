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

    private String engine;
    private String step;
    private String description;
    private String key;
    private Integer value;
    private Integer timeToRepetition;
    private CalibrationTimeout calibrationTimeout;

    @Before
    public void setUp() throws Exception {
        engine = "ode";
        step = "deploy";
        description = "maven";
        key = engine + "." + step + "." + description;
        value = 30_000;
        timeToRepetition = 5_000;
        calibrationTimeout = new CalibrationTimeout(new Timeout(engine, step, description, value, timeToRepetition));
    }

    @After
    public void tearDown() throws Exception {
        engine = null;
        step = null;
        description = null;
        key = null;
        value = null;
        timeToRepetition = null;
        calibrationTimeout = null;
    }

    @Test
    public void testConstructorWithTimeout(){
        Timeout timeout = new Timeout(engine, step, description, value, timeToRepetition);
        calibrationTimeout = new CalibrationTimeout(timeout);
        assertEquals(engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals(step, calibrationTimeout.getStepOrProcess());
        assertEquals(description, calibrationTimeout.getDescription());
        assertEquals(key, calibrationTimeout.getKey());
        assertEquals(value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals(new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals(new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals(timeToRepetition.intValue(), calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals(new BigDecimal(timeToRepetition.doubleValue() / 1000), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals(new BigDecimal(timeToRepetition.doubleValue() / 1000 / 60), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals(CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testConstructor(){
        calibrationTimeout = new CalibrationTimeout(engine, step, description, value, timeToRepetition);
        assertEquals(engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals(step, calibrationTimeout.getStepOrProcess());
        assertEquals(description, calibrationTimeout.getDescription());
        assertEquals(key, calibrationTimeout.getKey());
        assertEquals(value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals(new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals(new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals(timeToRepetition.intValue(), calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals(new BigDecimal(timeToRepetition.doubleValue() / 1000), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals(new BigDecimal(timeToRepetition.doubleValue() / 1000 / 60), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals(CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testConstructorWithoutDescription(){
        calibrationTimeout = new CalibrationTimeout(engine, step, value, timeToRepetition);
        assertEquals(engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals(step, calibrationTimeout.getStepOrProcess());
        assertEquals("", calibrationTimeout.getDescription());
        key = engine + "." + step;
        assertEquals(key, calibrationTimeout.getKey());
        assertEquals(value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals(new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals(new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals(timeToRepetition.intValue(), calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals(new BigDecimal(timeToRepetition.doubleValue() / 1000), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals(new BigDecimal(timeToRepetition.doubleValue() / 1000 / 60), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals(CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testConstructorWithoutTimeToRepetition(){
        calibrationTimeout = new CalibrationTimeout(engine, step, description, value);
        assertEquals(engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals(step, calibrationTimeout.getStepOrProcess());
        assertEquals(description, calibrationTimeout.getDescription());
        assertEquals(key, calibrationTimeout.getKey());
        assertEquals(value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals(new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals(new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals(0, calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals(new BigDecimal(0), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals(new BigDecimal(0), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals(CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testConstructorWithoutDescriptionTimeToRepetition(){
        calibrationTimeout = new CalibrationTimeout(engine, step, value);
        assertEquals(engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals(step, calibrationTimeout.getStepOrProcess());
        assertEquals("", calibrationTimeout.getDescription());
        key = engine + "." + step;
        assertEquals(key, calibrationTimeout.getKey());
        assertEquals(value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals(new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals(new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals(0, calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals(new BigDecimal(0), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals(new BigDecimal(0), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals(CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testGetStatusAfterCreation() throws Exception {
        assertEquals(CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testSetStatus() throws Exception {
        CalibrationTimeout.Status status = CalibrationTimeout.Status.KEPT;
        calibrationTimeout.setStatus(status);
        assertEquals(status, calibrationTimeout.getStatus());
    }

    @Test
    public void testGetStatus() throws Exception {
        CalibrationTimeout.Status status = CalibrationTimeout.Status.KEPT;
        calibrationTimeout.setStatus(status);
        assertEquals(status, calibrationTimeout.getStatus());
    }
}