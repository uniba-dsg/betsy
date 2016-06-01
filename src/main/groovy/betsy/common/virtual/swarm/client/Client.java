package betsy.common.virtual.swarm.client;

import betsy.common.virtual.calibration.Measurement;
import betsy.common.virtual.cbetsy.*;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import betsy.common.virtual.docker.Images;
import betsy.common.virtual.exceptions.DockerException;
import betsy.common.virtual.exceptions.NetworkException;
import betsy.common.virtual.swarm.common.ConnectionService;
import betsy.common.virtual.swarm.messages.DataMessage;
import betsy.common.virtual.swarm.messages.NumberMessage;
import com.google.common.io.Files;
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
public class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class);
    private DockerMachine dockerMachine;
    private TcpReceiver tcpReceiver;
    private int number;
    private ResourceConfiguration containerConfiguration;
    private Optional<String[]> arguments = Optional.empty();
    private Optional<List<WorkerTemplate>> templates = Optional.empty();
    private Optional<Boolean> isReady = Optional.empty();
    private Optional<Boolean> isFinished = Optional.of(false);

    /**
     * This method starts the client.
     *
     * @param args The arguments for starting the client.
     */
    public void start(String args[]) {
        if (args.length > 0) {
            try(Socket socket = new Socket(args[0], 9090)){
                ConnectionService connectionService = new ConnectionService(socket);
                tcpReceiver = new TcpReceiver(connectionService, this);
                tcpReceiver.start();
                boolean isCalibrated = false;
                while (!isFinished.get()) {
                    isFinished = Optional.empty();
                    if (!isCalibrated) {
                        calibrate(connectionService);
                        isCalibrated = true;
                    }
                    isFinished = work(connectionService);
                }
                tcpReceiver.setRunning(false);
                connectionService.close();
            } catch (NetworkException | IOException e) {
                LOGGER.info("The connection to the host failed.");
            }
        } else {
            printUsage();
        }
    }

    /**
     * This handles the calibration of the machine.
     *
     * @param connectionService The \{@link ConnectionService} handles the connection to the host.
     */
    private void calibrate(ConnectionService connectionService) {
        LOGGER.info("Waiting for arguments.");
        boolean isWaiting = true;
        while (isWaiting) {
            if (arguments.isPresent()) {
                isWaiting = false;
            }
        }
        LOGGER.info("Create the templates for the workers.");
        WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator(arguments.get());

        LOGGER.info("Start the dockerMachine and create the images.");
        dockerMachine = build(workerTemplateGenerator.getEngines());

        LOGGER.info("Start to evaluate the number of containers.");
        ResourceConfiguration systemResources = Measurement.measureResources();


        number = Measurement.calculateContainerNumber(systemResources, Measurement.evaluateMaxMemory(workerTemplateGenerator.getEnginesWithValues(dockerMachine)));
        containerConfiguration = Measurement.calculateResources(systemResources, number);

        if (Measurement.calibrateTimeouts(workerTemplateGenerator.getEngines(), dockerMachine, systemResources)) {
            try {
                connectionService.send(new NumberMessage(number));
            } catch (NetworkException e) {
                LOGGER.info("The connection to the host failed.");
                try {
                    tcpReceiver.setRunning(false);
                    connectionService.close();
                } catch (NetworkException e1) {
                    LOGGER.info(e.getMessage());
                }
            }
        }
    }

    /**
     * This method handles the processing of the workerTemplates.
     *
     * @param connectionService The {@link ConnectionService} to handle the connection to the host.
     * @return Returns the status of the process.
     */
    private Optional<Boolean> work(ConnectionService connectionService) {
        boolean isWaiting = true;
        while (isWaiting) {
            if (templates.isPresent()) {
                isWaiting = false;
            }
        }
        isWaiting = true;

        LOGGER.info("Start the spawner.");
        Spawner spawner = new Spawner(dockerMachine, templates.get(), containerConfiguration, number);
        List<Container> containers = spawner.start();

        LOGGER.info("Aggregation of the results.");
        Aggregator aggregator = new Aggregator(dockerMachine, containers);
        aggregator.start();

        LOGGER.info("Waiting for host.");
        while (isWaiting) {
            if (isReady.isPresent()) {
                isWaiting = false;
                try {
                    connectionService.send(new DataMessage(getFiles(Paths.get("results")), getDirectories(Paths.get("results"))));
                } catch (NetworkException e) {
                    LOGGER.info("The connection to the host failed.");
                    try {
                        tcpReceiver.setRunning(false);
                        connectionService.close();
                    } catch (NetworkException e1) {
                        LOGGER.info(e.getMessage());
                    }
                }
            }
        }
        isWaiting = true;

        LOGGER.info("Waiting for finish message.");
        while (isWaiting) {
            if (isFinished.isPresent()) {
                isWaiting = false;
            }
        }
        return isFinished;
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
     * With this method it is possbile to get the files of a directory.
     *
     * @param directory The directory of the files.
     * @return Returns a {@link HashMap} with the bytes the file path.
     */
    private static HashMap<String, byte[]> getFiles(Path directory) {
        HashMap<String, byte[]> files = new HashMap<>();
        ArrayList<File> fileList = getFilesInDirectory(directory);
        for (File file : fileList) {
            try {
                files.put(file.getPath(), Files.toByteArray(file));
            } catch (IOException e) {
                LOGGER.info("Can't read result files.");
            }
        }
        return files;
    }

    /**
     * With this method it is possible to collect files in a directory.
     *
     * @param directory The directory, where the files should be collected.
     */
    private static ArrayList<String> getDirectories(Path directory) {
        File[] files = directory.toFile().listFiles();
        ArrayList<String> directoryList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    directoryList.addAll(getDirectories(file.toPath()));
                    directoryList.add(file.toString());
                }
            }
        }
        return directoryList;
    }

    /**
     * With this method it is possible to collect files in a directory.
     *
     * @param directory The directory, where the files should be collected.
     */
    private static ArrayList<File> getFilesInDirectory(Path directory) {
        File[] files = directory.toFile().listFiles();
        ArrayList<File> fileList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(getFilesInDirectory(file.toPath()));
                } else {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * This method prints the right usage of the arguments.
     */
    private static void printUsage() {
        System.out.println("The fourth argument has to be the ip address of the host.");
    }

    /**
     * With this method it is possible to set the arguments fo the calibration.
     *
     * @param args Thr arguments for the calibration.
     */
    public void setArguments(String[] args) {
        arguments = Optional.ofNullable(args);
    }

    /**
     * With this method it is possible to set the workerTemplates.
     *
     * @param templates The workerTemplates to process.
     */
    public void setTemplates(List<WorkerTemplate> templates) {
        this.templates = Optional.ofNullable(templates);
    }

    /**
     * With this method it is possible to set the status of host.
     *
     * @param isReady The status of the host.
     */
    public void isReady(Boolean isReady) {
        this.isReady = Optional.ofNullable(isReady);
    }

    /**
     * With this method it is possible to set the status of process.
     *
     * @param isFinished The status of the process.
     */
    public void isFinished(Boolean isFinished) {
        this.isFinished = Optional.ofNullable(isFinished);
    }
}