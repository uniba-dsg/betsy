package betsy.common.virtual.calibration;

import betsy.common.virtual.Properties;
import betsy.common.virtual.WorkerTemplateGenerator;
import betsy.common.virtual.docker.*;
import org.apache.log4j.Logger;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
     * @param args The arguements to start the calibration.
     */
    public static void main(String... args) {
        DockerMachine dockerMachine = DockerMachines.create(get("dockermachine.run.name"), get("dockermachine.run.ram"), get("dockermachine.run.cpu"));
        dockerMachine.start();
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);

        HashMap<String, Boolean> engines = templateGenerator.getEngines();
        Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");

        engines.forEach((e, k) -> Images.buildEngine(dockerMachine, Paths.get("docker/image/engine").toAbsolutePath(), e));

        ArrayList<String[]> values = new ArrayList<>();
        templateGenerator.getEngines().forEach((k, t) -> values.add(measure(dockerMachine, k, t)));
        Properties.write(Paths.get("worker.properties"), values);

        LOGGER.info("Calibration finished.");
    }

    /**
     * Measures the duration and the peak memory usage during the execution of the sequence process in case of a BPELEngine
     * or the sequenceFLow process in case of a BPMNEngine on an engine.
     *
     * @param dockerMachine The dockermachine for the measurement.
     * @param engine        The engine for the measurement.
     * @param isBPELEngine  The attribute have to be true, if it's BPELEngine.
     * @return Returns a array with the engine, the time and the memory.
     */
    private static String[] measure(DockerMachine dockerMachine, String engine, boolean isBPELEngine) {
        Container container;
        if (isBPELEngine) {
            container = Containers.create(dockerMachine, "calibration_" + engine, engine.replace("_", ""), "bpel", engine, "sequence");
        } else {
            container = Containers.create(dockerMachine, "calibration_" + engine, engine.replace("_", ""), "bpmn", engine, "sequenceFlow");
        }
        long start;
        long end = 0;
        double memory = 0;
        start = System.currentTimeMillis();

        int position = 0;
        Scanner scanner = Tasks.doDockerTaskWithOutput(dockerMachine, "stats");
        container.start(true);
        int counter = 0;
        while (scanner.hasNextLine() && counter < 10) {
            String nextLine = scanner.nextLine();
            if (nextLine.contains(container.getId().substring(0, 11))) {
                counter = 0;
                end = System.currentTimeMillis();
                String resultText = nextLine.substring(position, nextLine.indexOf("/"));
                double result = Double.valueOf(resultText.replaceAll("[^0-9.]", ""));
                if (resultText.contains("GB")) {
                    result = result * 1000;
                }
                if (memory < result) {
                    memory = result;
                }
            } else if (nextLine.contains("CONTAINER")) {
                position = nextLine.indexOf("CPU %");
                counter++;
            }
        }
        return new String[]{engine, String.valueOf(end - start), String.valueOf(memory)};
    }
}
