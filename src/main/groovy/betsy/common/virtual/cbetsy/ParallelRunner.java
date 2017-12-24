package betsy.common.virtual.cbetsy;

import betsy.common.virtual.calibration.Measurement;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.Images;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          <p>
 *          This class manages the parallel execution of the containers.
 */
public class ParallelRunner {

    private static final Logger LOGGER = Logger.getLogger(ParallelRunner.class);
    private static Path docker = Paths.get(get("docker.dir"));
    private static long startBetsy = 0;
    private static long endBetsy = 0;
    private static long endEngines = 0;

    /**
     * The main method the execute the parallel execution.
     *
     * @param args The arguments for the execution.
     */
    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        LOGGER.info("Create the templates for the workers.");
        WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator(args);

        LOGGER.info("Start the dockerMachine and create the images.");
        build(workerTemplateGenerator.getEngines());
        long build = System.currentTimeMillis();

        LOGGER.info("Start to evaluate the number of containers.");
        ResourceConfiguration systemResources = Measurement.measureResources();
        int number = Measurement.calculateContainerNumber(systemResources, Measurement.evaluateMaxMemory(workerTemplateGenerator.getEnginesWithValues()));
        ResourceConfiguration containerConfiguration = Measurement.calculateResources(systemResources, number);
        long resources = System.currentTimeMillis();

        LOGGER.info("Start to calibrate the timeouts.");
        if (Measurement.calibrateTimeouts(workerTemplateGenerator.getEngines(), systemResources)) {
            long timeout = System.currentTimeMillis();

            LOGGER.info("Start the spawner.");
            Spawner spawner = new Spawner(workerTemplateGenerator.getSortedTemplates(), containerConfiguration, number);
            List<Container> containers = spawner.start();
            long execution = System.currentTimeMillis();

            LOGGER.info("Aggregation of the results.");
            Aggregator aggregator = new Aggregator(containers);
            aggregator.start();

            long end = System.currentTimeMillis();
            Reporter.createReport(workerTemplateGenerator, build - start, endBetsy - startBetsy, endEngines - endBetsy, resources - build, timeout - resources, execution - timeout, end - start);
        }
    }

    /**
     * This method creates the  betsy image and the images for the engines.
     *
     * @param engines The used engines.
     */
    private static void build(HashSet<DockerEngine> engines) {

        startBetsy = System.currentTimeMillis();
        Path image = docker.resolve("image");

        Images.build(image.resolve("betsy").toAbsolutePath(), "betsy");
        endBetsy = System.currentTimeMillis();
        engines.forEach(e -> Images.buildEngine(image.resolve("engine").toAbsolutePath().toAbsolutePath(), e.getName()));
        endEngines = System.currentTimeMillis();
    }
}
