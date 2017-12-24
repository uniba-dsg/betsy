package betsy.common.virtual.calibration;

import betsy.common.virtual.cbetsy.DockerEngine;
import betsy.common.virtual.cbetsy.WorkerTemplateGenerator;
import betsy.common.virtual.docker.Images;
import org.apache.log4j.Logger;

import java.nio.file.Paths;
import java.util.HashSet;

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
            build(templateGenerator.getEngines());
            Measurement.measureMemoriesAndTimes(templateGenerator.getEngines());
            Measurement.calibrateTimeouts(templateGenerator.getEngines(), Measurement.measureResources());
            LOGGER.info("Calibration finished.");
        }else{
            printUsage();
        }
    }

    /**
     * This method creates the betsy image and the images for the engines.
     *
     * @param engines The used engines.
     * @return Returns the created dockerMachine.
     */
    private static void build(HashSet<DockerEngine> engines) {
            Images.build(Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
            engines.forEach(e  -> Images.buildEngine(Paths.get("docker/image/engine").toAbsolutePath(), e.getName()));
    }

    /**
     *  This method prints the right usage of the arguments.
     */
    private static void printUsage() {
        System.out.println("The third argument must be bpel, bpmn");
    }
}
