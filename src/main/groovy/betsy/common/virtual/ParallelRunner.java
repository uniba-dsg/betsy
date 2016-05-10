package betsy.common.virtual;

import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import betsy.common.virtual.docker.Images;
import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.io.IOException;
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

        //create templates
        LOGGER.info("Create the templates for the workers.");
        WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator(args);

        //build
        LOGGER.info("Start the dockerMachine and create the images.");
        DockerMachine dockerMachine = build(workerTemplateGenerator.getEngines());

        //evaluate the resources
        int memory = evaluateMemory(workerTemplateGenerator.getWorkerTemplatesWithValues());
        int number = evaluateNumber(memory);
        if(number > 0){
            int cpuShares = 1260 / number;
            int hddSpeed = new Double(Measurement.execute(Paths.get("docker/hdd_test.txt"), 1000) / number).intValue();
            LOGGER.info("The results of the the resource evaluation are: " + memory + " mb;  cpushares: " + cpuShares + "; hddSpeed: " + hddSpeed + " mb/s; number of containers: " + number);


            LOGGER.info("Start to calibrate the timeouts.");
            Measurement.calibrateTimeouts(workerTemplateGenerator.getEngines(), dockerMachine, memory, cpuShares, hddSpeed);

            //start the
            LOGGER.info("Start the spawner.");
            Spawner spawner = new Spawner(dockerMachine, workerTemplateGenerator.getWorkerTemplatesWithValues(), number, cpuShares, memory, hddSpeed);
            List<Container> containers = spawner.start();

            LOGGER.info("Aggregation of the results.");
            Aggregator aggregator = new Aggregator(containers);
            aggregator.start();
        }else{
            LOGGER.info("The system hasn't enough free memory.");
        }

    }

    /**
     * This method creates the dockermachine, the betsy image and the images for the engines.
     *
     * @param engines The used engines.
     * @return Returns the created dockermachine.
     */
    private static DockerMachine build(HashMap<String, Boolean> engines) {
        DockerMachine dockerMachine = DockerMachines.create(get("dockermachine.run.name"), get("dockermachine.run.ram"), get("dockermachine.run.cpu"));
        dockerMachine.start();
        DockerMachine.Status status = DockerMachine.Status.STOPPED;
        try {
            status = dockerMachine.getStatus();
        } catch (DockerException e) {
            LOGGER.info("Can't evaluate the status of the dockerMachine: " + dockerMachine.getName());
        }
        if (status == DockerMachine.Status.STOPPED) {
            LOGGER.info("The dockermachine " + dockerMachine.getName() + " have to be started.");
            System.exit(0);
        } else {
            Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
            engines.forEach((e, k) -> Images.buildEngine(dockerMachine, Paths.get("docker/image/engine").toAbsolutePath(), e));
        }
        return dockerMachine;
    }

    /**
     * This method returns the maximum memory usage of all engines.
     *
     * @param workerTemplates The workerTemplates to evaluate.
     * @return The maximum memory usage as {@link Integer}.
     */
    private static Integer evaluateMemory(ArrayList<WorkerTemplate> workerTemplates) {
        List<Integer> memories = new ArrayList<>();
        workerTemplates.forEach(k -> memories.add(new Double(k.getMemory()).intValue()));
        return Collections.max(memories);
    }

    /**
     * This method calculates the number of containers, which can be executed parallel.
     *
     * @param memory The maximum memory usage of a container.
     * @return The number of containers as {@link Integer}.
     */
    private static Integer evaluateNumber(int memory) {
        ProcessBuilder  builder = new ProcessBuilder("free");
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            LOGGER.info("Cant' read free memory.");
        }
        Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
        int freeMemory = 0;
        while(scanner.hasNextLine()){
            String nextLine = scanner.nextLine();
            if(nextLine.contains("Speicher")){
                String[] parts = nextLine.split("\\s+");
                freeMemory = new Integer(parts[1]) - new Integer(parts[2]);
            }
        }

        int number;
        LOGGER.info("Free memory " + freeMemory /1000);
        number = Math.toIntExact(freeMemory / 1000 / memory);

        return number;
    }
}
