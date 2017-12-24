package betsy.common.virtual.cbetsy;

import betsy.common.analytics.model.Test;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.Containers;
import betsy.common.virtual.docker.Image;
import betsy.common.virtual.docker.Images;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class WorkerTest {

    @Before
    public void setUp() throws Exception {
        Images.build(Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
    }

    @After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void call() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        boolean betsyImageWasCreated = false;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            Images.build(Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        }
        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll().get("ode135"));
        boolean engineImageWasCreated = false;
        if (!engineImage.isPresent()) {
            engineImageWasCreated = true;
            Images.buildEngine(Paths.get("docker/image/engine").toAbsolutePath(), "ode__1_3_5");
        }

        Test process = Mockito.mock(Test.class);
        when(process.getName()).thenReturn("sequence");
        DockerEngine dockerEngine = new DockerEngine("ode__1_3_5", DockerEngine.TypeOfEngine.BPEL);

        Worker worker = new Worker(new WorkerTemplate(process.getName(), dockerEngine), 2000, 100);
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Container> container = executor.submit(worker);
        Container containerResult = container.get();
        assertNotNull("The container should not be null.", containerResult);

        Containers.remove(containerResult);

        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(engineImage.get());
        }

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(betsyImage.get());
        }
    }
}