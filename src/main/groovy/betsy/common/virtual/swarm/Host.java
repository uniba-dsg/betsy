package betsy.common.virtual.swarm;

import betsy.common.virtual.cbetsy.*;
import betsy.common.virtual.calibration.Measurement;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import betsy.common.virtual.docker.Images;
import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          This class manages the swarm processes.
 */
class Host {

    private static final Logger LOGGER = Logger.getLogger(Host.class);
    private static DockerMachine dockerMachine;
    private static ResourceConfiguration containerConfiguration;
    private static int number;
    private static ArrayList<HostConnectionHandler> connections = new ArrayList<>();
    private static HashMap<HostConnectionHandler, Integer> numbers = new HashMap<>();
    private static ArrayList<WorkerTemplate> workerTemplates = new ArrayList<>();
    private static long build = 0;
    private static long resources = 0;
    private static long timeout = 0;
    private long execution = 0;

    public void start(String args[]) {

        try {
            LOGGER.info("The host starts.");
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9090));
            serverSocketChannel.configureBlocking(false);

            int min = Integer.valueOf(get("docker.swarm.time"));
            long end = System.currentTimeMillis() + (min * 60 * 1000);
            LOGGER.info("Waiting " + min + "min for clients.");
            while (end >= System.currentTimeMillis()) {
                SocketChannel client = serverSocketChannel.accept();
                if(client != null){
                    HostConnectionHandler hostConnectionHandler = new HostConnectionHandler(client, this);
                    hostConnectionHandler.start();
                    connections.add(hostConnectionHandler);
                }
            }
            LOGGER.info("Create templates");
            WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator(args);
            workerTemplates.addAll(workerTemplateGenerator.getSortedTemplates());
            boolean isCalibrated = false;
            boolean isFinished = false;

            while (!isFinished) {
                work(workerTemplateGenerator, isCalibrated, args);
                isCalibrated = true;
                if (workerTemplates.size() == 0) {
                    connections.forEach(e -> e.sendFinishMessage(true));
                    isFinished = true;
                } else {
                    connections.forEach(e -> e.sendFinishMessage(false));
                }
            }
            LOGGER.info("Create reports.");
            Reporter.createReport(workerTemplateGenerator, build, resources, timeout, execution);
        } catch (IOException e) {
            LOGGER.info("Starting the host failed.");
        }

    }

    private void work(WorkerTemplateGenerator workerTemplateGenerator, boolean isCalibrated, String... args) {
        connections.forEach(e -> e.sendArgs(args));
        if (!isCalibrated) {

            LOGGER.info("Start the dockerMachine and create the images.");
            long startBuild = System.currentTimeMillis();
            dockerMachine = build(workerTemplateGenerator.getEngines());
            build = build + System.currentTimeMillis() - startBuild;

            LOGGER.info("Start to evaluate the number of containers.");
            long startResources = System.currentTimeMillis();
            ResourceConfiguration systemResources = Measurement.measureResources();
            number = Measurement.calculateContainerNumber(systemResources, Measurement.evaluateMaxMemory(workerTemplateGenerator.getEnginesWithValues()));
            containerConfiguration = Measurement.calculateResources(systemResources, number);
            resources = resources + System.currentTimeMillis() - startResources;

            LOGGER.info("Start to calibrate the timeouts.");
            long startTimeout = System.currentTimeMillis();
            //TODO:
            Measurement.calibrateTimeouts(workerTemplateGenerator.getEngines(), dockerMachine, systemResources);
            timeout = timeout + System.currentTimeMillis() - startTimeout;

            LOGGER.info("Wait for the clients.");
            boolean wait = true;
            while (wait) {
                if (numbers.size() == connections.size()) {
                    wait = false;
                }
            }
        }

        LOGGER.info("Split templates.");
        int sumContainer = number;
        for (Integer number : numbers.values()) {
            sumContainer = sumContainer + number;
        }
        LOGGER.info("The hole system executes " + sumContainer + " container parallel.");

        int start = 0;
        for (HostConnectionHandler hostConnectionHandler : numbers.keySet()) {
            int end = workerTemplates.size() * (numbers.get(hostConnectionHandler) / sumContainer);
            hostConnectionHandler.sendWorkerTemplates(workerTemplates.subList(start, end));
            start = end;
        }
        List<WorkerTemplate> ownTemplates = workerTemplates.subList(start, workerTemplates.size());
        workerTemplates = new ArrayList<>();

        LOGGER.info("Start the spawner.");
        long startExecution = System.currentTimeMillis();
        Spawner spawner = new Spawner(dockerMachine, ownTemplates, containerConfiguration, number);
        List<Container> containers = spawner.start();
        execution = execution + System.currentTimeMillis() - startExecution;

        LOGGER.info("Aggregation of the results.");
        Aggregator aggregator = new Aggregator(dockerMachine, containers);
        aggregator.start();

        LOGGER.info("Wait for the clients.");
        boolean wait = true;
        while (wait) {
            if (connections.stream().filter(HostConnectionHandler::isReady).count() == connections.size()) {
                wait = false;
            }
        }
        wait = true;
        connections.forEach(HostConnectionHandler::sendStartDataMessage);

        LOGGER.info("Wait for the clients.");
        //wait for others execution
        while (wait) {
            if (connections.stream().filter(HostConnectionHandler::isReady).count() == connections.size()) {
                wait = false;
            }
        }

    }

    /**
     * With this method it is possible to remove a failed client.
     *
     * @param hostConnectionHandler The connectionHandler, which failed.
     * @param workerTemplates   The workerTemplates, which handles the client.
     */
    public synchronized void connectionFailed(HostConnectionHandler hostConnectionHandler, List<WorkerTemplate> workerTemplates) {
        connections.remove(hostConnectionHandler);
        numbers.remove(hostConnectionHandler);
        Host.workerTemplates.addAll(workerTemplates);
    }

    /**
     * With this method it is possible to add the number of containers, which can handle
     * the client to the same time.
     *
     * @param hostConnectionHandler The concerned connectionHandler.
     * @param number            The number of containers.
     */
    public synchronized void addNumber(HostConnectionHandler hostConnectionHandler, int number) {
        numbers.put(hostConnectionHandler, number);
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
