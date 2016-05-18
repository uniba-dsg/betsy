package betsy.common.virtual.cbetsy;

import betsy.common.virtual.cbetsy.Aggregator;
import betsy.common.virtual.docker.*;
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

    private DockerMachine dockerMachine;
    private ArrayList<Container> containers = new ArrayList<>();
    private Path docker = Paths.get(get("docker.dir"));
    private Path images = docker.resolve("images");
    private java.util.Optional<Image> betsyImage;
    private boolean betsyImageWasCreated = true;

    @Before
    public void setUp() throws Exception {
        dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
        betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));

        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = false;
            Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy");
        }
        containers.add(Containers.run(dockerMachine, "test", "betsy", "sh", "betsy", "bpel", "ode", "sequence"));
    }

    @After
    public void tearDown() throws Exception {
        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(dockerMachine, betsyImage.get());
        }
        DockerMachines.remove(dockerMachine);
    }

    @Test
    public void start() throws Exception {
        Aggregator aggregator = new Aggregator(dockerMachine, containers);
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