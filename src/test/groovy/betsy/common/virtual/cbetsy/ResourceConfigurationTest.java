package betsy.common.virtual.cbetsy;

import betsy.common.virtual.cbetsy.ResourceConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Christoph Broeker
 * @version 1.0
 */
public class ResourceConfigurationTest {


    @Test
    public void getMemory() throws Exception {
        int memory = 2000;
        int hddSpeed = 200;
        ResourceConfiguration resourceConfiguration = new ResourceConfiguration(memory, hddSpeed);
        assertEquals("The values memory values be equal.", memory, resourceConfiguration.getMemory());
    }

    @Test
    public void getHddSpeed() throws Exception {
        int memory = 2000;
        int hddSpeed = 200;
        ResourceConfiguration resourceConfiguration = new ResourceConfiguration(memory, hddSpeed);
        assertEquals("The values of the hddSpeed should be equal", hddSpeed, resourceConfiguration.getHddSpeed());
    }
}