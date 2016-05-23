package betsy.common.virtual.docker;

import org.junit.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class ContainerTest {


    private static DockerMachine dockerMachine;
    private Container container;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));

    }

    @AfterClass
    public static void setUpAfterClass() throws Exception {
        java.util.Optional<Image> ubuntu = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
        if (ubuntu.isPresent()) {
            Images.remove(dockerMachine, ubuntu.get());
        }
        DockerMachines.remove(dockerMachine);
    }

    @Before
    public void setUp() throws Exception {
        container = Containers.create(dockerMachine,"test", "ubuntu", "sleep", "60");
    }

    @After
    public void tearDown() throws Exception {
        container.stop();
        Containers.remove(dockerMachine,container);
    }

    @Test
    public void restart() throws Exception {
        container.start(true);
        assertEquals("The container should have the status 'RUNNING'", Container.Status.RUNNING, container.getStatus());
        container.restart();
        assertEquals("The container should have the status 'RUNNING'", Container.Status.RUNNING, container.getStatus());
    }

    @Test
    public void start() throws Exception {
        container.start(true);
        assertEquals("The container should have the status 'RUNNING'.", Container.Status.RUNNING, container.getStatus());
    }

    @Test
    public void  startSilent() throws Exception {
        container.start(false);
        assertEquals("The container should have the status 'EXITED'.", Container.Status.EXITED, container.getStatus());
    }

    @Test
    public void stop() throws Exception {
        container.start(true);
        assertEquals("The container should have the status 'EXITED'.", Container.Status.RUNNING, container.getStatus());
        container.stop();
        assertEquals("The container should have the status 'EXITED'.", Container.Status.EXITED, container.getStatus());
    }

    @Test
    public void exec() throws Exception {
        String cmd = "sleep";
        String cmd1 = "1m";
        container.start(true);
        container.exec(cmd, cmd1);
        assertEquals("The container should have the status 'RUNNING'.", Container.Status.RUNNING, container.getStatus());
    }

    @Test
    public void getId() throws Exception {
        assertNotNull("The container should have an id.", container.getId());
    }

    @Test
    public void getName() throws Exception {
        String name = "test";
        container = Containers.create(dockerMachine, name, "Ubuntu", "sleep 10m");
        assertEquals("The names should be equal.", name, container.getName());
    }

    @Test
    public void getStatus() throws Exception {
        container = Containers.create(dockerMachine,"test", "ubuntu", "sleep", "6000");
        assertEquals("The container should have the status 'RUNNING'.", Container.Status.CREATED, container.getStatus());
    }

    @Test
    public void copyToContainer() throws Exception {
        Path path = Paths.get("betsy");
        container.copyToContainer(path, Paths.get("/betsy"));
        container.copyFromContainer(Paths.get("betsy"), Paths.get("betsy"));
        Path testFilePath = Paths.get("docker/betsy");
        assertTrue("The file should exist.", path.toFile().exists());
        testFilePath.toFile().delete();
    }

    @Test
    public void copyFromContainer() throws Exception {
        Path path = Paths.get("betsy");
        container.copyToContainer(path, Paths.get("/betsy"));
        container.copyFromContainer(Paths.get("betsy"), Paths.get("betsy"));
        Path testFilePath = Paths.get("docker/betsy");
        assertTrue("The file should exist.", path.toFile().exists());
        testFilePath.toFile().delete();
    }


}