package betsy.common.virtual.cbetsy;


import betsy.common.virtual.cbetsy.ResourceConfiguration;
import betsy.common.virtual.cbetsy.Spawner;
import betsy.common.virtual.cbetsy.WorkerTemplateGenerator;
import betsy.common.virtual.docker.*;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class SpawnerTest {

    @Test
    public void start() throws Exception {
        Path docker = Paths.get(get("docker.dir"));
        Path images = docker.resolve("image");

        DockerMachine dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasCreated = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = false;
            Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        }
        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ode135"));
        boolean engineImageWasCreated = true;
        if (!engineImage.isPresent()) {
            engineImageWasCreated = false;
            Images.buildEngine(dockerMachine, images.resolve("engine").toAbsolutePath(), "ode__1_3_5");
        }

        WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator("bpel", "ode", "sequence");
        Spawner spawner = new Spawner(dockerMachine, workerTemplateGenerator.getSortedTemplates(), new ResourceConfiguration( 1000, 1000), 4);
        List<Container> containers = spawner.start();
        assertEquals("One container should exist.", 1, containers.size());

        java.util.Optional<Container> container = java.util.Optional.ofNullable(Containers.getAll(dockerMachine).get("ode135sequence"));
        if (container.isPresent()) {
            Containers.remove(dockerMachine, container.get());
        }

        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(dockerMachine, engineImage.get());
        }

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(dockerMachine, betsyImage.get());
        }
        DockerMachines.remove(dockerMachine);
    }
}