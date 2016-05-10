package betsy.common.virtual.docker;

import org.junit.Test;

import java.util.HashMap;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class DockerMachinesTest {


    @Test
    public void testCreate() throws Exception {
        DockerMachine dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
        assertEquals("The machines should be equal.", dockerMachine, DockerMachines.get(dockerMachine.getName()));
        DockerMachines.remove(dockerMachine);
    }

    @Test
    public void testRemove() throws Exception {
        DockerMachine dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
        DockerMachines.remove(dockerMachine);
        assertNull("The machine should be null.", DockerMachines.get(dockerMachine.getName()));
    }

    @Test
    public void testGet() throws Exception {
        DockerMachine dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
        assertEquals("The machines should be equal.", dockerMachine, DockerMachines.get(dockerMachine.getName()));
        DockerMachines.remove(dockerMachine);
    }

    @Test
    public void testGetAll() throws Exception {
        DockerMachine dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
        HashMap<String, DockerMachine> dockerMachines = DockerMachines.getAll();
        assertEquals("The machines should be equal.", dockerMachine, dockerMachines.get(dockerMachine.getName()));
        DockerMachines.remove(dockerMachine);
    }
}