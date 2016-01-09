package timeouts.timeout;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutTest {

    private Timeout timeout;
    private String engine;
    private String step;
    private String description;
    private Timeout.Category category;
    private Timeout.PlaceOfUse placeOfUse;
    private Integer value;
    private Integer timeToRepetition;

    @Before
    public void setUp() throws Exception {
        engine = "ode";
        step = "deploy";
        description = "maven";
        category = Timeout.Category.UNMEASURABLE;
        placeOfUse = Timeout.PlaceOfUse.EXTERN;
        value = 90_000;
        timeToRepetition = 500;
        timeout = new Timeout(engine, step, description, value, timeToRepetition, category, placeOfUse);
    }

    @After
    public void tearDown() throws Exception {
        engine = null;
        step = null;
        description = null;
        category = null;
        placeOfUse = null;
        value = null;
        timeToRepetition = null;
        timeout = null;
    }

    @Test
    public void testConstructor(){
        timeout = new Timeout(engine, step, value);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals("", timeout.getDescription());
        assertEquals(Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals(Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorAllValues(){
        timeout = new Timeout(engine, step, description, value, timeToRepetition);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals(description, timeout.getDescription());
        assertEquals(Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals(Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithCategory(){
        timeout = new Timeout(engine, step, value, category);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals("", timeout.getDescription());
        assertEquals(category, timeout.getCategory());
        assertEquals(Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithPlaceOfUse(){
        timeout = new Timeout(engine, step, value, placeOfUse);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals("", timeout.getDescription());
        assertEquals(Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals(placeOfUse, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithCategoryPlaceOfUse(){
        timeout = new Timeout(engine, step, value, category, placeOfUse);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals("", timeout.getDescription());
        assertEquals(category, timeout.getCategory());
        assertEquals(placeOfUse, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescription(){
        timeout = new Timeout(engine, step, description, value);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals(description, timeout.getDescription());
        assertEquals(Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals(Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionCategory(){
        timeout = new Timeout(engine, step, description, value, category);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals(description, timeout.getDescription());
        assertEquals(category, timeout.getCategory());
        assertEquals(Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionPlaceOfUse(){
        timeout = new Timeout(engine, step, description, value, placeOfUse);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals(description, timeout.getDescription());
        assertEquals(Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals(placeOfUse, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionCategoryPlaceOfUse(){
        timeout = new Timeout(engine, step, description, value, category, placeOfUse);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals(description, timeout.getDescription());
        assertEquals(category, timeout.getCategory());
        assertEquals(placeOfUse, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithTimeToRepetition(){
        timeout = new Timeout(engine, step, value, timeToRepetition);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals("", timeout.getDescription());
        assertEquals(Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals(Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithTimeToRepetitionCategory(){
        timeout = new Timeout(engine, step, value, timeToRepetition, category);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals("", timeout.getDescription());
        assertEquals(category, timeout.getCategory());
        assertEquals(Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithTimeToRepetitionPlaceOfUse(){
        timeout = new Timeout(engine, step, value, timeToRepetition, placeOfUse);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals("", timeout.getDescription());
        assertEquals(Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals(placeOfUse, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithTimeToRepetitionCategoryPlaceOfUse(){
        timeout = new Timeout(engine, step, value, timeToRepetition,category, placeOfUse);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals("", timeout.getDescription());
        assertEquals(category, timeout.getCategory());
        assertEquals(placeOfUse, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionTimeToRepetitionCategory(){
        timeout = new Timeout(engine, step, description, value, timeToRepetition, category);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals(description, timeout.getDescription());
        assertEquals(category, timeout.getCategory());
        assertEquals(Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionTimeToRepetitionPlaceOfUse(){
        timeout = new Timeout(engine, step, description, value, timeToRepetition, placeOfUse);
        assertEquals(engine, timeout.getEngineOrProcessGroup());
        assertEquals(step, timeout.getStepOrProcess());
        assertEquals(description, timeout.getDescription());
        assertEquals(Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals(placeOfUse, timeout.getPlaceOfUse());
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
        assertEquals(timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testGetKey() throws Exception {
        StringBuilder key = new StringBuilder(engine);
        assertEquals(key.append(".").append(step).append(".").append(description).toString(), timeout.getKey());
    }

    @Test
    public void testGetKeyWithoutDescription() throws Exception {
        timeout = new Timeout(engine, step, value, timeToRepetition);
        StringBuilder key = new StringBuilder(engine);
        assertEquals(key.append(".").append(step).toString(), timeout.getKey());
    }

    @Test
    public void testGetEngineOrProcessGroup() throws Exception {
        assertEquals(engine, timeout.getEngineOrProcessGroup());
    }

    @Test
    public void testGetStepOrProcess() throws Exception {
        assertEquals(step, timeout.getStepOrProcess());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals(description, timeout.getDescription());
    }

    @Test
    public void testSetValue() throws Exception {
        int newValue = 40_000;
        timeout.setValue(newValue);
        assertEquals(newValue, timeout.getTimeoutInMs());
    }

    @Test
    public void testGetTimeoutsInMinutes() throws Exception {
        assertEquals(new BigDecimal(value.doubleValue() / 1000 / 60), timeout.getTimeoutInMinutes());
    }

    @Test
    public void testGetTimeoutInSeconds() throws Exception {
        assertEquals(new BigDecimal(value.doubleValue() / 1000), timeout.getTimeoutInSeconds());
    }

    @Test
    public void testGetTimeoutInMs() throws Exception {
        assertEquals(value.intValue(), timeout.getTimeoutInMs());
    }

    @Test
    public void testSetTimeToRepetition() throws Exception {
        int newTimeToRepetitionValue = 30_000;
        timeout.setTimeToRepetition(newTimeToRepetitionValue);
        assertEquals(newTimeToRepetitionValue, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testGetTimeToRepetitionInMinutesIsNull() throws Exception {
        timeout = new Timeout(engine, step, value);
        assertEquals(new BigDecimal(0), timeout.getTimeToRepetitionInMinutes());
    }

    @Test
    public void testGetTimeToRepetitionInSecondsIsNull() throws Exception {
        timeout = new Timeout(engine, step, value);
        assertEquals(new BigDecimal(0), timeout.getTimeToRepetitionInSeconds());
    }

    @Test
    public void testGetTimeToRepetitionInMsIsNull() throws Exception {
        timeout = new Timeout(engine, step, value);
        assertEquals(0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testGetCategory(){
        assertEquals(category, timeout.getCategory());
    }

    @Test
    public void testGetPlaceOfUse(){
        assertEquals(placeOfUse, timeout.getPlaceOfUse());
    }

    @Test
    public void testSetCategory(){
        timeout.setCategory(Timeout.Category.UNMEASURABLE);
        assertEquals(category, timeout.getCategory());
    }

    @Test
    public void testSetPlaceOfUse(){
        timeout.setPlaceOfUse(Timeout.PlaceOfUse.EXTERN);
        assertEquals(placeOfUse, timeout.getPlaceOfUse());
    }
}