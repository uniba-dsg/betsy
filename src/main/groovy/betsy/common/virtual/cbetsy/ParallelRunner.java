package betsy.common.virtual.cbetsy;

import betsy.common.virtual.calibration.Measurement;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import betsy.common.virtual.docker.Images;
import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.nio.file.Paths;
import java.util.*;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          <p>
 *          This class manages the parallel execution of the containers.
 */
public class ParallelRunner {

    private static final Logger LOGGER = Logger.getLogger(ParallelRunner.class);

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
        DockerMachine dockerMachine = build(workerTemplateGenerator.getEngines());
        long build = System.currentTimeMillis();

        LOGGER.info("Start to evaluate the number of containers.");
        ResourceConfiguration systemResources = Measurement.measureResources();
        int number = Measurement.calculateContainerNumber(systemResources, Measurement.evaluateMaxMemory(workerTemplateGenerator.getEnginesWithValues()));
        ResourceConfiguration containerConfiguration = Measurement.calculateResources(systemResources, number);
        long resources = System.currentTimeMillis();

        LOGGER.info("Start to calibrate the timeouts.");
        if(Measurement.calibrateTimeouts(workerTemplateGenerator.getEngines(), dockerMachine, systemResources)){
            long timeout = System.currentTimeMillis();

            LOGGER.info("Start the spawner.");
            Spawner spawner = new Spawner(dockerMachine, workerTemplateGenerator.getSortedTemplates(), containerConfiguration, number);
            List<Container> containers = spawner.start();
            long execution = System.currentTimeMillis();

            LOGGER.info("Aggregation of the results.");
            Aggregator aggregator = new Aggregator(dockerMachine, containers);
            aggregator.start();

            Reporter.createReport(workerTemplateGenerator, build-start, resources-build, timeout-resources, execution-timeout);
        }
    }

    /**
     * This method creates the dockerMachine, the betsy image and the images for the engines.
     *
     * @param engines The used engines.
     * @return Returns the created dockerMachine.
     */
    private static DockerMachine build(HashSet<DockerEngine> engines) {
        DockerMachine dockerMachine = DockerMachines.create(get("dockermachine.run.name"), get("dockermachine.run.ram"), get("dockermachine.run.cpu"));
        dockerMachine.start();
        DockerMachine.Status status = DockerMachine.Status.STOPPED;
        try {
            status = dockerMachine.getStatus();
        } catch (DockerException e) {
            LOGGER.info("Can't evaluate the status of the dockerMachine: " + dockerMachine.getName());
        }
        if (status == DockerMachine.Status.STOPPED) {
            LOGGER.info("The dockerMachine " + dockerMachine.getName() + " have to be started.");
            System.exit(0);
        } else {
            Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
            engines.forEach(e -> Images.buildEngine(dockerMachine, Paths.get("docker/image/engine").toAbsolutePath(), e.getName()));
        }
        return dockerMachine;
    }
}
