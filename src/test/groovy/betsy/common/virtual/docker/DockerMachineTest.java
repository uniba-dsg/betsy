package betsy.common.virtual.docker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class DockerMachineTest {

    private DockerMachine dockerMachine;
    private String name;

    @Before
    public void setUp() throws Exception {
        name = get("dockermachine.test.name");
        dockerMachine = DockerMachines.create(name, get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
    }

    @After
    public void tearDown() throws Exception {
        DockerMachines.remove(dockerMachine);
        dockerMachine = null;
    }

    @Test
    public void testStart() throws Exception {
        dockerMachine.start();
        assertEquals("The statuses should be equal.", DockerMachine.Status.RUNNING, dockerMachine.getStatus());
        dockerMachine.stop();
    }

    @Test
    public void testStop() throws Exception {
        dockerMachine.start();
        dockerMachine.stop();
        if(System.getProperty("os.name").contains("Windows")) {
            assertEquals("The statuses should be equal.", DockerMachine.Status.STOPPED, dockerMachine.getStatus());
        }else{
            assertEquals("The statuses should be equal.", DockerMachine.Status.RUNNING, dockerMachine.getStatus());
        }
    }

    @Test
    public void testGetNameOfTheMachine() throws Exception {
        assertEquals("The names should be equal.", name, dockerMachine.getName());
        dockerMachine.stop();
    }

    @Test
    public void testGetStatus() throws Exception {
        assertEquals("After creating the machine should be stopped.", DockerMachine.Status.RUNNING, dockerMachine.getStatus());
        dockerMachine.stop();
    }
}