package betsy.common.virtual.docker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    public void testCreate() throws Exception {
        String containerName = "test";
        Container container = Containers.create(dockerMachine, containerName, "ubuntu", "sleep 10m" );
        assertEquals("The name of the containers should be the same.", container.getName(), Containers.getAll(dockerMachine).get(containerName).getName());
    }

    @Test
    public void testCreateConstraints() throws Exception {
        String containerName = "test";
        Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        Container container = Containers.create(dockerMachine, containerName, "betsy", 120, 4000, 300, "calibrate", "bpel", "ode", "sequence");
        assertEquals("The name of the containers should be the same.", container.getName(), Containers.getAll(dockerMachine).get(containerName).getName());
    }

    @Test
    public void testRemove() throws Exception {
        String containerName = "test";
        Container container = Containers.create(dockerMachine, containerName, "hello-world");
        Containers.remove(dockerMachine, container);
        assertNull("The container should be null.", Containers.getAll(dockerMachine).get(container.getName()));
    }

    @Test
    public void testRemoveAll() throws Exception {
        Containers.removeAll(dockerMachine, new ArrayList<>(Containers.getAll(dockerMachine).values()));
        assertEquals("The container should be null.", 0, Containers.getAll(dockerMachine).size());
    }

    @Test
    public void testGetAll() throws Exception {
        String containerName = "test";
        Container container = Containers.create(dockerMachine, containerName, "hello-world");
        HashMap<String, Container> containerHashMap = Containers.getAll(dockerMachine);
        assertEquals("The containers should be the same", container, containerHashMap.get(containerName));
    }


    @Test
    public void testRun() throws Exception {
        String containerName = "test";
        Container container = Containers.run(dockerMachine, containerName, "ubuntu", "sleep 10m" );
        assertEquals("The name of the containers should be the same.", container.getName(), Containers.getAll(dockerMachine).get(containerName).getName());
    }

    @Test
    public void testRunConstraints() throws Exception {
        String containerName = "test";
        Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        Container container = Containers.run(dockerMachine, containerName, "betsy", 120, 4000, 300, "calibrate", "bpel", "ode", "sequence");
        assertEquals("The name of the containers should be the same.", container.getName(), Containers.getAll(dockerMachine).get(containerName).getName());
    }
}