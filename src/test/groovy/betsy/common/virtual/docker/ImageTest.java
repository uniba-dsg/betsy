package betsy.common.virtual.docker;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class ImageTest {

    @Test
    public void getId() throws Exception {
        String id = "test";
        String name = "test";
        Image image = new Image(id, name);
        assertEquals("The ids should be equal.", id, image.getId());
    }

    @Test
    public void getName() throws Exception {
        String id = "test";
        String name = "test";
        Image image = new Image(id, name);
        assertEquals("The names should be equal.", name, image.getName());
    }

}