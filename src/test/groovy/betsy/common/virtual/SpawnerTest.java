package betsy.common.virtual;


import betsy.common.virtual.docker.Containers;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import org.junit.Test;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class SpawnerTest {

    @Test
    public void start() throws Exception {
        DockerMachine dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
        WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator("bpel", "ode", "sequence");
        Spawner spawner = new Spawner(dockerMachine, workerTemplateGenerator.getWorkerTemplatesWithValues(), 1, 1260, 2000, 150);
        spawner.start();
        assertEquals("One container should exist.", 1, Containers.getAll(dockerMachine).size());
        DockerMachines.remove(dockerMachine);
    }



}