package betsy.common.virtual.cbetsy;

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
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasCreated = false;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        }
        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ode135"));
        boolean engineImageWasCreated = false;
        if (!engineImage.isPresent()) {
            engineImageWasCreated = true;
            Images.buildEngine(dockerMachine, Paths.get("docker/image/engine").toAbsolutePath(), "ode__1_3_5");
        }

        EngineIndependentProcess process = Mockito.mock(EngineIndependentProcess.class);
        when(process.getName()).thenReturn("sequence");
        DockerEngine dockerEngine = new DockerEngine("ode__1_3_5", DockerEngine.TypeOfEngine.BPEL);

        Worker worker = new Worker(dockerMachine, new WorkerTemplate(process.getName(), dockerEngine), 2000, 100);
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Container> container = executor.submit(worker);
        Container containerResult = container.get();
        assertNotNull("The container should not be null.", containerResult);

        Containers.remove(dockerMachine, containerResult);

        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(dockerMachine, engineImage.get());
        }

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(dockerMachine, betsyImage.get());
        }
    }
}