package betsy.common.timeouts.calibration;

import java.math.BigDecimal;

import betsy.common.timeouts.timeout.Timeout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class CalibrationTimeoutTest {

    private String engine;
    private String step;
    private Integer value;


    @Before
    public void setUp() throws Exception {
        engine = "ode";
        step = "deploy";
        value = 30_000;
    }

    @After
    public void tearDown() throws Exception {
        engine = null;
        step = null;
        value = null;
    }

    @Test
    public void testConstructorWithTimeout(){
        String description = "maven";
        Integer timeToRepetition = 5_000;
        String key = engine + "." + step + "." + description;
        Timeout timeout = new Timeout(engine, step, description, value, timeToRepetition);
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(timeout);
        assertEquals("The engines should be equal.", engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, calibrationTimeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, calibrationTimeout.getDescription());
        assertEquals("The keys should be equal.", key, calibrationTimeout.getKey());
        assertEquals("The values in ms should be equal.", value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals("The values in sec should be equal.", new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals("The values in min should be equal.", new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals("The timeToRepetition in ms should be equal.", timeToRepetition.intValue(), calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals("The timeToRepetition in sec should be equal.", new BigDecimal(timeToRepetition.doubleValue() / 1000), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals("The timeToRepetition in min should be equal.", new BigDecimal(timeToRepetition.doubleValue() / 1000 / 60), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals("The statuses should be equal.", CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testConstructor(){
        String description = "maven";
        Integer timeToRepetition = 5_000;
        String key = engine + "." + step + "." + description;
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(engine, step, description, value, timeToRepetition);
        assertEquals("The engines should be equal.", engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, calibrationTimeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, calibrationTimeout.getDescription());
        assertEquals("The keys should be equal.", key, calibrationTimeout.getKey());
        assertEquals("The values in ms should be equal.", value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals("The values in sec should be equal.", new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals("The values in min should be equal.", new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals("The timeToRepetition in ms should be equal.", timeToRepetition.intValue(), calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals("The timeToRepetition in sec should be equal.", new BigDecimal(timeToRepetition.doubleValue() / 1000), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals("The timeToRepetition in min should be equal.", new BigDecimal(timeToRepetition.doubleValue() / 1000 / 60), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals("The statuses should be equal.", CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testConstructorWithoutDescription(){
        Integer timeToRepetition = 5_000;
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(engine, step, value, timeToRepetition);
        String key = engine + "." + step;
        assertEquals("The engines should be equal.", engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, calibrationTimeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", "", calibrationTimeout.getDescription());
        assertEquals("The keys should be equal.", key, calibrationTimeout.getKey());
        assertEquals("The values in ms should be equal.", value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals("The values in sec should be equal.", new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals("The values in min should be equal.", new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals("The timeToRepetition in ms should be equal.", timeToRepetition.intValue(), calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals("The timeToRepetition in sec should be equal.", new BigDecimal(timeToRepetition.doubleValue() / 1000), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals("The timeToRepetitions in min should be equal.", new BigDecimal(timeToRepetition.doubleValue() / 1000 / 60), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals("The statuses should be equal.", CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testConstructorWithoutTimeToRepetition(){
        String description = "maven";
        String key = engine + "." + step + "." + description;
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(engine, step, description, value);
        assertEquals("The engines should be equal.", engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, calibrationTimeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, calibrationTimeout.getDescription());
        assertEquals("The keys should be equal.", key, calibrationTimeout.getKey());
        assertEquals("The values in ms should be equal.", value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals("The values in sec should be equal.", new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals("The values in min should be equal.", new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals("The timeToRepetitions in ms should be equal.", 0, calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals("The timeToRepetitions in sec should be equal.", new BigDecimal(0), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals("The timeToRepetitions in min should be equal.", new BigDecimal(0), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals("The statuses should be equal.", CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testConstructorWithoutDescriptionTimeToRepetition(){
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(engine, step, value);
        String key = engine + "." + step;
        assertEquals("The engines should be equal.", engine, calibrationTimeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, calibrationTimeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", calibrationTimeout.getDescription());
        assertEquals("The keys should be equal.", key, calibrationTimeout.getKey());
        assertEquals("The values in ms should be equal.", value.intValue() , calibrationTimeout.getTimeoutInMs());
        assertEquals("The values in sec should be equal.", new BigDecimal(value.doubleValue() / 1000), calibrationTimeout.getTimeoutInSeconds());
        assertEquals("The values in min should be equal.", new BigDecimal(value.doubleValue() / 1000 / 60), calibrationTimeout.getTimeoutInMinutes());
        assertEquals("The timeToRepetitions in ms should be equal.", 0, calibrationTimeout.getTimeToRepetitionInMs());
        assertEquals("The timeToRepetitions in sec should be equal.", new BigDecimal(0), calibrationTimeout.getTimeToRepetitionInSeconds());
        assertEquals("The timeToRepetitions in min should be equal.", new BigDecimal(0), calibrationTimeout.getTimeToRepetitionInMinutes());
        assertEquals("The statuses should be equal.", CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testGetStatusAfterCreation() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(new Timeout(engine, step, value));
        assertEquals("After creation the status should be 'kept'.",CalibrationTimeout.Status.KEPT, calibrationTimeout.getStatus());
    }

    @Test
    public void testSetStatus() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(new Timeout(engine, step, value));
        CalibrationTimeout.Status status = CalibrationTimeout.Status.EXCEEDED;
        calibrationTimeout.setStatus(status);
        assertEquals("The statuses should be equal.", status, calibrationTimeout.getStatus());
    }

    @Test
    public void testGetStatus() throws Exception {
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(new Timeout(engine, step, value));
        CalibrationTimeout.Status status = CalibrationTimeout.Status.KEPT;
        calibrationTimeout.setStatus(status);
        assertEquals("The statuses should be equal.", status, calibrationTimeout.getStatus());
    }

    @Test
    public void testGetCalibrationTimeoutKey() throws  Exception{
        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(new Timeout(engine, step, value));
        assertEquals("The keys should be equal.", calibrationTimeout.getKey() + "." + calibrationTimeout.getTimestamp(), calibrationTimeout.getCalibrationTimeoutKey());
    }
}
