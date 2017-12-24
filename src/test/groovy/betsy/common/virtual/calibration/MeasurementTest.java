package betsy.common.virtual.calibration;

import betsy.common.virtual.cbetsy.DockerEngine;
import betsy.common.virtual.cbetsy.ResourceConfiguration;
import betsy.common.virtual.cbetsy.WorkerTemplateGenerator;
import betsy.common.virtual.docker.Image;
import betsy.common.virtual.docker.Images;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class MeasurementTest {
    private Path docker = Paths.get(get("docker.dir"));
    private Path images = docker.resolve("image");

    @Test
    public void calibrateTimeouts() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        boolean betsyImageWasCreated = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = false;
            betsyImage = java.util.Optional.ofNullable(Images.build(images.resolve("betsy").toAbsolutePath(), "betsy"));
        }
        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll().get("ode136"));
        boolean engineImageWasCreated = true;

        if (!engineImage.isPresent()) {
            engineImageWasCreated = false;
            engineImage = java.util.Optional.ofNullable(Images.buildEngine(images.resolve("engine").toAbsolutePath(), "ode__1_3_6"));
        }
        ResourceConfiguration resourceConfiguration = new ResourceConfiguration(2000, 100);
        WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator("bpel", "ode", "sequence");
        assertTrue("The method returns true, if the calibration was successful.", Measurement.calibrateTimeouts(workerTemplateGenerator.getEnginesWithValues(), resourceConfiguration));
        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(engineImage.get());
        }

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(betsyImage.get());
        }
    }

    @Test
    public void measureMemoriesAndTimes() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        boolean betsyImageWasCreated = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = false;
            betsyImage = java.util.Optional.ofNullable(Images.build(images.resolve("betsy").toAbsolutePath(), "betsy"));
        }
        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll().get("ode136"));
        boolean engineImageWasCreated = true;

        if (!engineImage.isPresent()) {
            engineImageWasCreated = false;
            engineImage = java.util.Optional.ofNullable(Images.buildEngine(images.resolve("engine").toAbsolutePath(), "ode__1_3_6"));
        }
        WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator("bpel", "ode", "sequence");
        Measurement.measureMemoriesAndTimes(workerTemplateGenerator.getEngines());
        ArrayList<DockerEngine> engines = new ArrayList<>(workerTemplateGenerator.getEnginesWithValues());
        assertTrue("The memory have to be greater than 0.", engines.get(0).getMemory() > 0);

        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(engineImage.get());
        }

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(betsyImage.get());
        }
    }

    @Test
    public void measureResources() throws Exception {
        assertNotNull("The mehtod should not return a null value.", Measurement.measureResources());
    }

    @Test
    public void calculateContainerNumber() throws Exception {
        int memory = 2000;
        int hdd = 200;
        int containerMemory = 1000;
        assertEquals("The values should be equal.", memory / containerMemory, Measurement.calculateContainerNumber(new ResourceConfiguration(memory, hdd), containerMemory));
    }

    @Test
    public void calculateResources() throws Exception {
        int memory = 2000;
        int hhd = 200;
        int number = 10;
        ResourceConfiguration resourceConfiguration = new ResourceConfiguration(memory, hhd);
        ResourceConfiguration configuration = Measurement.calculateResources(resourceConfiguration, number);
        assertEquals("The memory values have to equal", memory / number, configuration.getMemory());
        assertEquals("The hdd values have to equal", hhd / number, configuration.getHddSpeed());
    }

    @Test
    public void evaluateMaxMemory() throws Exception {
        DockerEngine engine = new DockerEngine("engine", DockerEngine.TypeOfEngine.BPEL);
        int memory = 2000;
        engine.setMemory(memory);
        DockerEngine testEngine = new DockerEngine("testEngine", DockerEngine.TypeOfEngine.BPEL);
        testEngine.setMemory(1000);
        HashSet<DockerEngine> engines = new HashSet<>();
        engines.add(engine);
        engines.add(testEngine);
        assertEquals("The values have to be equal.", memory, Measurement.evaluateMaxMemory(engines).intValue());
    }

}