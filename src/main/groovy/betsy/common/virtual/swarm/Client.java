package betsy.common.virtual.swarm;

import betsy.common.virtual.calibration.Measurement;
import betsy.common.virtual.cbetsy.*;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import betsy.common.virtual.docker.Images;
import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          This class represents a client for the swarm.
 */
class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class);

    public void start(String args[]) {
        if (args.length > 0) {
            try {
                Optional<Boolean> isFinished = Optional.empty();
                ClientConnectionHandler host = new ClientConnectionHandler(new Socket(args[0], 9090));
                host.start();

                while (!isFinished.isPresent()) {
                    LOGGER.info("Waiting for arguments.");
                    Optional<String[]> arguments = Optional.empty();
                    while (!arguments.isPresent()) {
                        arguments = host.getArgs();
                    }

                    LOGGER.info("Create the templates for the workers.");
                    WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator(arguments.get());

                    LOGGER.info("Start the dockerMachine and create the images.");
                    DockerMachine dockerMachine = build(workerTemplateGenerator.getEngines());

                    LOGGER.info("Start to evaluate the number of containers.");
                    ResourceConfiguration systemResources = Measurement.measureResources();


                    int number = Measurement.calculateContainerNumber(systemResources, Measurement.evaluateMaxMemory(workerTemplateGenerator.getEnginesWithValues()));
                    ResourceConfiguration containerConfiguration = Measurement.calculateResources(systemResources, number);
                    if (Measurement.calibrateTimeouts(workerTemplateGenerator.getEngines(), dockerMachine, systemResources)) {
                        host.sendNumber(number);

                        LOGGER.info("Waiting for templates.");
                        Optional<List<WorkerTemplate>> workerTemplates = Optional.empty();
                        while (!arguments.isPresent()) {
                            workerTemplates = host.getWorkerTemplates();
                        }

                        //start the
                        LOGGER.info("Start the spawner.");
                        Spawner spawner = new Spawner(dockerMachine, workerTemplates.get(), containerConfiguration, number);
                        List<Container> containers = spawner.start();

                        LOGGER.info("Aggregation of the results.");
                        Aggregator aggregator = new Aggregator(dockerMachine, containers);
                        aggregator.start();
                        host.sendBoolean(true);

                        //TODO: collect files
                        LOGGER.info("Send files.");
                        host.sendData(true);
                        while (!isFinished.isPresent()) {
                            isFinished = host.getFinished();
                        }
                        if(!isFinished.get()){
                            isFinished = Optional.empty();
                        }
                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
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

    /**
     *
     * With this method it is possible to collect files in a directory.
     *
     * @param directory The directory, where the files should be collected.
     */
    private static ArrayList<File> displayDirectoryContents(Path directory) {
        File[] files = directory.toFile().listFiles();
        ArrayList<File> fileList = new ArrayList<>();
        if (files != null) {
            fileList = new ArrayList<>(Arrays.asList(files));
        }
        for (File file : fileList) {
            if (file.isDirectory()) {
                fileList.addAll(displayDirectoryContents(file.toPath()));
            }
        }
        return  fileList;
    }

    /**
     * This method prints the right usage of the arguments.
     */
    private static void printUsage() {
        System.out.println("The third argument has to be the ip address of the host.");
    }
}