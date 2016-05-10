package betsy.common.virtual.docker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static betsy.common.config.Configuration.get;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class ImagesTest {

    private DockerMachine dockerMachine;

    @Before
    public void setUp() throws Exception {
        dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
    }

    @After
    public void tearDown() throws Exception {
        DockerMachines.remove(dockerMachine);
    }

    @Test
    public void buildTest() throws Exception {
        Image image = Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        assertEquals("The method should return two images.", 2, Images.getAll(dockerMachine).size());
        Images.remove(dockerMachine, image);
    }

    @Test
    public void buildEngineImageTest() throws Exception {
        Image image = Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        Image engine = Images.buildEngine(dockerMachine, Paths.get("docker/image/engine").toAbsolutePath(), "ode__1_3_5");
        assertEquals("The method should return three images.", 3, Images.getAll(dockerMachine).size());
        Images.remove(dockerMachine, image);
        Images.remove(dockerMachine, engine);
    }

    @Test
    public void removeImageTest() throws Exception {
        Image image = Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        Images.remove(dockerMachine, image);
        assertNull("The image should be null.", Images.getAll(dockerMachine).get(image.getName()));
    }

    @Test
    public void getAllTest() throws Exception {
        Image image = Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        assertEquals("The method should return two images.", 2, Images.getAll(dockerMachine).size());
        Images.remove(dockerMachine, image);
    }
}