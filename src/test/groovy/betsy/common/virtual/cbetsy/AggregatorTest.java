package betsy.common.virtual.cbetsy;

import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.Containers;
import betsy.common.virtual.docker.Image;
import betsy.common.virtual.docker.Images;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class AggregatorTest {

        private ArrayList<Container> containers = new ArrayList<>();
    private Path docker = Paths.get(get("docker.dir"));
    private Path images = docker.resolve("image");
    private java.util.Optional<Image> betsyImage;
    private boolean betsyImageWasCreated = true;

    @Before
    public void setUp() throws Exception {

        betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = false;
            Images.build(images.resolve("betsy").toAbsolutePath(), "betsy");
        }
        containers.add(Containers.run("test", "betsy", "sh", "betsy", "bpel", "ode", "sequence"));
    }

    @After
    public void tearDown() throws Exception {
        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(betsyImage.get());
        }
            }

    @Test
    public void start() throws Exception {
        Aggregator aggregator = new Aggregator(containers);
        aggregator.start();
        File file1 = new File("results/" + containers.get(0).getName() + "/betsy.log");
        File file2 = new File("results/" + containers.get(0).getName() + "/betsy_time.log");
        File file3 = new File("results/" + containers.get(0).getName() + "/betsy_console.log");
        File directory = new File("results/" + containers.get(0).getName());
        assertTrue("The file should exist.", file1.exists());
        assertTrue("The file should exist.", file2.exists());
        assertTrue("The file should exist.", file3.exists());
        file1.delete();
        file2.delete();
        file3.delete();
        directory.delete();
    }

}