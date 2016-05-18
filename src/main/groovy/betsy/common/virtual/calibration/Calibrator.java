package betsy.common.virtual.calibration;

import betsy.common.virtual.cbetsy.DockerEngine;
import betsy.common.virtual.cbetsy.WorkerTemplateGenerator;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import betsy.common.virtual.docker.Images;
import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.nio.file.Paths;
import java.util.HashSet;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          With this class it is possible to calibrate the duration for an engine.
 */
public class Calibrator {

    private static final Logger LOGGER = Logger.getLogger(Calibrator.class);

    /**
     * This method starts the calibration.
     *
     * @param args The arguments to start the calibration.
     */
    public static void main(String... args) {
        if(args.length > 0){
            LOGGER.info("Started calibration.");
            WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
            DockerMachine dockerMachine = build(templateGenerator.getEngines());
            Measurement.measureMemoriesAndTimes(dockerMachine, templateGenerator.getEngines());
            Measurement.calibrateTimeouts(templateGenerator.getEngines(), dockerMachine, Measurement.measureResources());
            LOGGER.info("Calibration finished.");
        }else{
            printUsage();
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
            engines.forEach(e  -> Images.buildEngine(dockerMachine, Paths.get("docker/image/engine").toAbsolutePath(), e.getName()));
        }
        return dockerMachine;
    }

    /**
     *  This method prints the right usage of the arguments.
     */
    private static void printUsage() {
        System.out.println("The third argument must be bpel, bpmn");
    }
}
