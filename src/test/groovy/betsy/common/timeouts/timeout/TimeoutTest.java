package betsy.common.timeouts.timeout;

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

    private String engine;
    private String step;
    private Integer value;


    @Before
    public void setUp() throws Exception {
        engine = "ode";
        step = "deploy";
        value = 90_000;
    }

    @After
    public void tearDown() throws Exception {
        engine = null;
        step = null;
        value = null;
    }

    @Test
    public void testConstructorKey(){
        Timeout timeout = new Timeout(engine+"."+step, value);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithCategory(){
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine+"."+step, value, category);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.UNMEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithPlaceOfUse(){
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine+"."+step, value, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.EXTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithCategoryPlaceOfUse(){
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine+"."+step, value, category, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.UNMEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.EXTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithTimeToRepetition(){
        Integer timeToRepetition = 500;
        Timeout timeout = new Timeout(engine+"."+step, value, timeToRepetition);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 500.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithTimeToRepetitionCategory(){
        Integer timeToRepetition = 500;
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine+"."+step, value, timeToRepetition, category);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.UNMEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 500..", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithTimeToRepetitionPlaceOfUse(){
        Integer timeToRepetition = 500;
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine+"."+step, value, timeToRepetition, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.EXTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 500.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithTimeToRepetitionCategoryPlaceOfUse(){
        Integer timeToRepetition = 500;
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine+"."+step, value, timeToRepetition, category, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.UNMEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.EXTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 500.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyDescription(){
        String description = "maven";
        Timeout timeout = new Timeout(engine+"."+step+"."+description, value);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithCategoryDescription(){
        String description = "maven";
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine+"."+step+"."+description, value, category);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.UNMEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithPlaceOfUseDescription(){
        String description = "maven";
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine+"."+step+"."+description, value, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.EXTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithCategoryPlaceOfUseDescription(){
        String description = "maven";
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine+"."+step+"."+description, value, category, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.UNMEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.EXTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithTimeToRepetitionDescription(){
        String description = "maven";
        Integer timeToRepetition = 500;
        Timeout timeout = new Timeout(engine+"."+step+"."+description, value, timeToRepetition);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 500.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithTimeToRepetitionCategoryDescription(){
        String description = "maven";
        Integer timeToRepetition = 500;
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine+"."+step+"."+description, value, timeToRepetition, category);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.UNMEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 500.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithTimeToRepetitionPlaceOfUseDescription(){
        String description = "maven";
        Integer timeToRepetition = 500;
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine+"."+step+"."+description, value, timeToRepetition, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.EXTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 500.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorKeyWithTimeToRepetitionCategoryPlaceOfUseDescription(){
        String description = "maven";
        Integer timeToRepetition = 500;
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine+"."+step+"."+description, value, timeToRepetition, category, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.UNMEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.EXTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 500.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructor(){
        Timeout timeout = new Timeout(engine, step, value);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorAllValues(){
        String description = "maven";
        Integer timeToRepetition = 500;
        Timeout timeout = new Timeout(engine, step, description, value, timeToRepetition);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithCategory(){
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine, step, value, category);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", category, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithPlaceOfUse(){
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine, step, value, placeOfUse);
        assertEquals("The engines should be equal.",  engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", placeOfUse, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithCategoryPlaceOfUse(){
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine, step, value, category, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", category, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", placeOfUse, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescription(){
        String description = "maven";
        Timeout timeout = new Timeout(engine, step, description, value);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionCategory(){
        String description = "maven";
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine, step, description, value, category);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", category, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionPlaceOfUse(){
        String description = "maven";
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine, step, description, value, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", placeOfUse, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionCategoryPlaceOfUse(){
        String description = "maven";
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine, step, description, value, category, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", category, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", placeOfUse, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions should be 0, if no value is set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithTimeToRepetition(){
        Integer timeToRepetition = 500;
        Timeout timeout = new Timeout(engine, step, value, timeToRepetition);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithTimeToRepetitionCategory(){
        Integer timeToRepetition = 500;
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine, step, value, timeToRepetition, category);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", category, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithTimeToRepetitionPlaceOfUse(){
        Integer timeToRepetition = 500;
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine, step, value, timeToRepetition, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", placeOfUse, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithTimeToRepetitionCategoryPlaceOfUse(){
        Integer timeToRepetition = 500;
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine, step, value, timeToRepetition,category, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", "", timeout.getDescription());
        assertEquals("The categories should be equal.", category, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", placeOfUse, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionTimeToRepetitionCategory(){
        String description = "maven";
        Integer timeToRepetition = 500;
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine, step, description, value, timeToRepetition, category);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be empty.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", category, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", Timeout.PlaceOfUse.INTERN, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testConstructorWithDescriptionTimeToRepetitionPlaceOfUse(){
        String description = "maven";
        Integer timeToRepetition = 500;
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine, step, description, value, timeToRepetition, placeOfUse);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
        assertEquals("The categories should be equal.", Timeout.Category.MEASURABLE, timeout.getCategory());
        assertEquals("The placeOfUse should be equal.", placeOfUse, timeout.getPlaceOfUse());
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
        assertEquals("The timeToRepetitions in ms should be equal.", timeToRepetition.intValue(), timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testGetKey() throws Exception {
        String description = "maven";
        Timeout timeout = new Timeout(engine, step, description,  value);
        StringBuilder key = new StringBuilder(engine);
        assertEquals("The keys should be equal.", key.append(".").append(step).append(".").append(description).toString(), timeout.getKey());
    }

    @Test
    public void testGetKeyWithoutDescription() throws Exception {
        Integer timeToRepetition = 500;
        Timeout timeout = new Timeout(engine, step, value, timeToRepetition);
        StringBuilder key = new StringBuilder(engine);
        assertEquals("The keys should be equal.", key.append(".").append(step).toString(), timeout.getKey());
    }

    @Test
    public void testGetEngineOrProcessGroup() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        assertEquals("The engines should be equal.", engine, timeout.getEngineOrProcessGroup());
    }

    @Test
    public void testGetStepOrProcess() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        assertEquals("The steps should be equal.", step, timeout.getStepOrProcess());
    }

    @Test
    public void testGetDescription() throws Exception {
        String description = "maven";
        Timeout timeout = new Timeout(engine, step, description, value);
        assertEquals("The descriptions should be equal.", description, timeout.getDescription());
    }

    @Test
    public void testSetValue() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        int newValue = 40_000;
        timeout.setValue(newValue);
        assertEquals("The values in ms should be equal.",  newValue, timeout.getTimeoutInMs());
    }

    @Test
    public void testGetTimeoutsInMinutes() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        assertEquals("The values in min should be equal.", new BigDecimal(value.doubleValue() / 1000 / 60), timeout.getTimeoutInMinutes());
    }

    @Test
    public void testGetTimeoutInSeconds() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        assertEquals("The values in sec should be equal.", new BigDecimal(value.doubleValue() / 1000), timeout.getTimeoutInSeconds());
    }

    @Test
    public void testGetTimeoutInMs() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        assertEquals("The values in ms should be equal.", value.intValue(), timeout.getTimeoutInMs());
    }

    @Test
    public void testSetTimeToRepetition() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        int newTimeToRepetitionValue = 30_000;
        timeout.setTimeToRepetition(newTimeToRepetitionValue);
        assertEquals("The timeToRepetitions in ms should be equal.", newTimeToRepetitionValue, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testGetTimeToRepetitionInMinutesIsNull() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        assertEquals("The timeToRepetition in min should be 0, if there was no value set.", new BigDecimal(0), timeout.getTimeToRepetitionInMinutes());
    }

    @Test
    public void testGetTimeToRepetitionInSecondsIsNull() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        assertEquals("The timeToRepetition in sec should be 0, if there was no value set.", new BigDecimal(0), timeout.getTimeToRepetitionInSeconds());
    }

    @Test
    public void testGetTimeToRepetitionInMsIsNull() throws Exception {
        Timeout timeout = new Timeout(engine, step, value);
        assertEquals("The timeToRepetition in ms should be 0, if there was no value set.", 0, timeout.getTimeToRepetitionInMs());
    }

    @Test
    public void testGetCategory(){
        Timeout.Category category = Timeout.Category.UNMEASURABLE;
        Timeout timeout = new Timeout(engine, step, value, category);
        assertEquals("The categories should be equal.", category, timeout.getCategory());
    }

    @Test
    public void testGetPlaceOfUse(){
        Timeout.PlaceOfUse placeOfUse = Timeout.PlaceOfUse.EXTERN;
        Timeout timeout = new Timeout(engine, step, value, placeOfUse);
        assertEquals("The placeOfUse should be equal.", placeOfUse, timeout.getPlaceOfUse());
    }
}
