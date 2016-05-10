package betsy.common.virtual.docker;

import org.junit.*;

import java.io.File;
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
        DockerMachines.remove(dockerMachine);
    }

    @Before
    public void setUp() throws Exception {
        container = Containers.create(dockerMachine,"test", "ubuntu", "sleep", "180");
    }

    @After
    public void tearDown() throws Exception {
        container.stop();
        Containers.remove(dockerMachine,container);
    }

    @Test
    public void testRestart() throws Exception {
        container.start(true);
        assertEquals("The container should have the status 'RUNNING'", Container.Status.RUNNING, container.getStatus());
        container.restart();
        assertEquals("The container should have the status 'RUNNING'", Container.Status.RUNNING, container.getStatus());
    }

    @Test
    public void testStart() throws Exception {
        container.start(true);
        assertEquals("The container should have the status 'RUNNING'.", Container.Status.RUNNING, container.getStatus());
    }

    @Test
    public void testStartSilent() throws Exception {
        container.start(false);
        assertEquals("The container should have the status 'EXITED'.", Container.Status.EXITED, container.getStatus());
    }

    @Test
    public void testStop() throws Exception {
        container.start(true);
        assertEquals("The container should have the status 'EXITED'.", Container.Status.RUNNING, container.getStatus());
        container.stop();
        assertEquals("The container should have the status 'EXITED'.", Container.Status.EXITED, container.getStatus());
    }

    @Test
    public void testExec() throws Exception {
        String cmd = "sleep";
        String cmd1 = "1m";
        container.start(true);
        container.exec(cmd, cmd1);
        assertEquals("The container should have the status 'RUNNING'.", Container.Status.RUNNING, container.getStatus());
    }

    @Test
    public void testGetId() throws Exception {
        assertNotNull("The container should have an id.", container.getId());
    }

    @Test
    public void testGetName() throws Exception {
        String name = "test";
        container = Containers.create(dockerMachine, name, "Ubuntu", "sleep 10m");
        assertEquals("The names should be equal.", name, container.getName());
    }

    @Test
    public void testGetStatus() throws Exception {
        container = Containers.create(dockerMachine,"test", "ubuntu", "sleep", "6000");
        assertEquals("The container should have the status 'RUNNING'.", Container.Status.CREATED, container.getStatus());
    }

    @Test
    public void testGetCmd() throws Exception {
        container = Containers.create(dockerMachine,"test", "ubuntu", "sleep", "3000");
        assertEquals("The container should have the cmd 'sleep 180'.", "sleep 180", container.getCmd());
    }

    @Test
    public void copyToContainer() throws Exception {
        File file = new File("betsy");
        container.copyToContainer(file.getAbsoluteFile(), Paths.get("/betsy"));
        container.copyFromContainer(new File("betsy"), Paths.get("betsy"));
        File testFile = new File("docker/betsy");
        assertTrue("The file should exist.", file.exists());
        testFile.delete();
    }

    @Test
    public void copyFromContainer() throws Exception {
        File file = new File("betsy");
        container.copyToContainer(file.getAbsoluteFile(), Paths.get("/betsy"));
        container.copyFromContainer(new File("betsy"), Paths.get("betsy"));
        File testFile = new File("docker/betsy");
        assertTrue("The file should exist.", file.exists());
        testFile.delete();
    }


}