package betsy.common.virtual;

import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static betsy.common.config.Configuration.get;
import static junit.framework.TestCase.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class MeasurementTest {

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
    public void calibrateTimeouts() throws Exception {
        HashMap<String, Boolean> engines = new HashMap<>();
        engines.put("ode__1_3_6", true);
        Measurement.calibrateTimeouts(engines, dockerMachine, 2000, 1260, 100);
        Path path = Paths.get("docker/ode__1_3_6");
        assertTrue("The file should exist.", path.toFile().exists());
        path.toFile().delete();
    }

    @Test
    public void execute() throws Exception {
       assertTrue("The value should be greater 0.", Measurement.execute(Paths.get("docker/hdd_test.txt"), 1000) > 0);
    }

}