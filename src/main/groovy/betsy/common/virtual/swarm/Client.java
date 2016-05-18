package betsy.common.virtual.swarm;

import betsy.common.virtual.Aggregator;
import betsy.common.virtual.Spawner;
import betsy.common.virtual.WorkerTemplate;
import betsy.common.virtual.WorkerTemplateGenerator;
import betsy.common.virtual.DockerEngine;
import betsy.common.virtual.calibration.Measurement;
import betsy.common.virtual.ResourceConfiguration;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import betsy.common.virtual.docker.Images;
import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.io.*;
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

    public static void main(String args[]) {
        if (args.length > 0) {
            try {
                Socket client = new Socket(args[0], 9090);
                boolean isFinished = false;

                while (!isFinished) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                    String[] arguments = (String[]) objectInputStream.readObject();

                    LOGGER.info("Create the templates for the workers.");
                    WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator(arguments);

                    LOGGER.info("Start the dockerMachine and create the images.");
                    DockerMachine dockerMachine = build(workerTemplateGenerator.getEngines());

                    LOGGER.info("Start to evaluate the number of containers.");
                    ResourceConfiguration systemResources = Measurement.measureResources();
                    int number = Measurement.calculateContainerNumber(systemResources, Measurement.evaluateMaxMemory(workerTemplateGenerator.getEnginesWithValues()));
                    ResourceConfiguration containerConfiguration = Measurement.calculateResources(systemResources, number);
                    if(Measurement.calibrateTimeouts(workerTemplateGenerator.getEngines(), dockerMachine, systemResources)){
                        objectOutputStream.writeInt(number);

                        LOGGER.info("Waiting for templates.");
                        List<WorkerTemplate> workerTemplateList = (List<WorkerTemplate>) objectInputStream.readObject();

                        //start the
                        LOGGER.info("Start the spawner.");
                        Spawner spawner = new Spawner(dockerMachine, workerTemplateList, containerConfiguration, number);
                        List<Container> containers = spawner.start();

                        LOGGER.info("Aggregation of the results.");
                        Aggregator aggregator = new Aggregator(dockerMachine, containers);
                        aggregator.start();
                        objectOutputStream.writeBoolean(true);

                        objectInputStream.readBoolean();
                        objectOutputStream.close();

                        LOGGER.info("Send files.");
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(client.getOutputStream());
                        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

                        ArrayList<File> files = displayDirectoryContents(Paths.get("results"));
                        dataOutputStream.writeInt(files.size());

                        for (File file : files) {
                            long length = file.length();
                            dataOutputStream.writeLong(length);
                            String name = file.getName();
                            dataOutputStream.writeUTF(name);
                            FileInputStream fileInputStream = new FileInputStream(file);
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                            int theByte = 0;
                            while ((theByte = bufferedInputStream.read()) != -1) bufferedOutputStream.write(theByte);

                            bufferedInputStream.close();
                        }
                        dataOutputStream.close();
                    }
                    isFinished = objectInputStream.readBoolean();
                    objectInputStream.close();
                    objectOutputStream.close();
                }
            } catch (Exception e) {
                LOGGER.info("The connection to the host failed.");
            }
        } else {
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