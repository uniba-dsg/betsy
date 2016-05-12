package betsy.common.virtual;

import betsy.bpel.engines.bpelg.BpelgEngine;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.virtual.docker.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class WorkerTest {

    private DockerMachine dockerMachine;

    @Before
    public void setUp() throws Exception {
        dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
        Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
    }

    @After
    public void tearDown() throws Exception {
        DockerMachines.remove(dockerMachine);
    }

    @Test
    public void call() throws Exception {
        EngineIndependentProcess process = Mockito.mock(EngineIndependentProcess.class);
        when(process.getName()).thenReturn("sequence");

        Worker worker = new Worker(dockerMachine, new WorkerTemplate(process, new BpelgEngine()), 200, 2000, 100);
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Container> container = executor.submit(worker);
        assertNotNull("The container should not be null.", container.get());
    }
}