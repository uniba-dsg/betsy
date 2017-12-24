package betsy.common.virtual.docker;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class ContainersTest {


    private Path docker = Paths.get(get("docker.dir"));
    private Path images = docker.resolve("image");

        @Test
    public void create() throws Exception {
        String containerName = "test";
        Container container = Containers.create(containerName, "ubuntu", "sleep 10m" );
        assertEquals("The name of the containers should be the same.", container.getName(), Containers.getAll().get(containerName).getName());
        Containers.remove(container);
    }


    @Test
    public void createConstraints() throws Exception {
        String containerName = "test";
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        boolean betsyImageWasCreated = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = false;
            betsyImage = java.util.Optional.ofNullable(Images.build(images.resolve("betsy").toAbsolutePath(), "betsy"));
        }
        Container container = Containers.create(containerName, "betsy", 4000, 300, "calibrate", "bpel", "ode", "sequence");
        assertEquals("The name of the containers should be the same.", container.getName(), Containers.getAll().get(containerName).getName());
        Containers.remove(container);
        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(betsyImage.get());
        }
    }

    @Test
    public void remove() throws Exception {
        String containerName = "test";
        Container container = Containers.create(containerName, "hello-world");
        Containers.remove(container);
        assertNull("The container should be null.", Containers.getAll().get(container.getName()));
    }

    @Test
    public void removeAll() throws Exception {
        Container container = Containers.create("test", "hello-world");
        int size = Containers.getAll().size();
        ArrayList<Container> containers = new ArrayList<>();
        containers.add(container);
        Containers.removeAll(containers);
        assertEquals("The container should be null.", --size, Containers.getAll().size());

    }

    @Test
    public void getAll() throws Exception {
        String containerName = "test";
        Container container = Containers.create(containerName, "hello-world");
        HashMap<String, Container> containerHashMap = Containers.getAll();
        assertEquals("The containers should be the same", container, containerHashMap.get(containerName));
    }


    @Test
    public void run() throws Exception {
        String containerName = "test";
        Container container = Containers.run(containerName, "ubuntu", "sleep 10m" );
        assertEquals("The name of the containers should be the same.", container.getName(), Containers.getAll().get(containerName).getName());
        Containers.remove(container);
    }

    @Test
    public void runConstraints() throws Exception {
        String containerName = "test";
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        boolean betsyImageWasCreated = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = false;
            betsyImage = java.util.Optional.ofNullable(Images.build(images.resolve("betsy").toAbsolutePath(), "betsy"));
        }
        Images.build(images.resolve("betsy").toAbsolutePath(), "betsy");
        Container container = Containers.run(containerName, "betsy", 4000, 300, "bpel", "ode", "sequence");
        assertEquals("The name of the containers should be the same.", container.getName(), Containers.getAll().get(containerName).getName());
        Containers.remove(container);
        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(betsyImage.get());
        }
    }
}