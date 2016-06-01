package betsy.common.virtual.swarm.host;

import betsy.common.virtual.calibration.Measurement;
import betsy.common.virtual.cbetsy.*;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.DockerMachines;
import betsy.common.virtual.docker.Images;
import betsy.common.virtual.exceptions.DockerException;
import betsy.common.virtual.swarm.common.ConnectionService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
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
public class Host {

    private static final Logger LOGGER = Logger.getLogger(Host.class);
    private TcpMessageService tcpMessageService;
    private static DockerMachine dockerMachine;
    private static ResourceConfiguration containerConfiguration;
    private static int number;
    private static long build = 0;
    private static long startBetsy = 0;
    private static long endBetsy = 0;
    private static long endEngines = 0;
    private static long resources = 0;
    private static long timeout = 0;
    private long execution = 0;

    /**
     *
     * This method starts the host.
     *
     * @param args The arguments for starting the host.
     */
    public void start(String args[]) {
        try {
            long start = System.currentTimeMillis();
            LOGGER.info("The host starts.");
            ConnectionHandler connectionHandler = new ConnectionHandler(new ServerSocket(9090));
            connectionHandler.start();
            int min = Integer.valueOf(get("docker.swarm.time"));
            long endTime = System.currentTimeMillis() + (min * 20 * 1000);
            LOGGER.info("Waiting " + min + " min for clients.");
            boolean isWaiting = true;
            while (isWaiting) {
                if (endTime > System.currentTimeMillis()) {
                    isWaiting = true;
                } else {
                    isWaiting = false;
                    tcpMessageService = connectionHandler.finish();
                }
            }

            LOGGER.info("Create templates");
            WorkerTemplateGenerator workerTemplateGenerator = new WorkerTemplateGenerator(args);
            ArrayList<WorkerTemplate> templates = workerTemplateGenerator.getWorkerTemplates();

            boolean isCalibrated = false;
            boolean isFinished = false;
            while (!isFinished) {
                if (!isCalibrated) {
                    calibrate(workerTemplateGenerator, args);
                    isCalibrated = true;
                }
                if (templates.size() == 0) {
                    tcpMessageService.sendFinishMessage(true);
                    isFinished = true;
                } else {
                    work(templates);
                    templates = tcpMessageService.getReturnedTemplates();
                    if (templates.size() > 0) {
                        tcpMessageService.sendFinishMessage(false);
                        isFinished = false;
                    }
                }
            }
            long end = System.currentTimeMillis();
            LOGGER.info("Create reports.");
            Reporter.createReport(workerTemplateGenerator, build-start, endBetsy-startBetsy, endEngines-endBetsy,  resources-build, timeout-resources, execution-timeout, end-start);
        } catch (IOException e) {
            LOGGER.info("Starting the host failed.");
        }

    }

    /**
     *
     * This method manages the calibration.
     *
     * @param workerTemplateGenerator The workerTemplates for the calibration.
     * @param args The arguments for the clients.
     */
    private void calibrate(WorkerTemplateGenerator workerTemplateGenerator, String... args) {
        LOGGER.info("Send arguments to clients.");
        tcpMessageService.sendArgsMessage(args);

        LOGGER.info("Start the dockerMachine and create the images.");
        long startBuild = System.currentTimeMillis();
        dockerMachine = build(workerTemplateGenerator.getEngines());
        build = build + System.currentTimeMillis() - startBuild;

        LOGGER.info("Start to evaluate the number of containers.");
        long startResources = System.currentTimeMillis();
        ResourceConfiguration systemResources = Measurement.measureResources();
        number = Measurement.calculateContainerNumber(systemResources, Measurement.evaluateMaxMemory(workerTemplateGenerator.getEnginesWithValues(dockerMachine)));
        containerConfiguration = Measurement.calculateResources(systemResources, number);
        resources = resources + System.currentTimeMillis() - startResources;

        LOGGER.info("Start to calibrate the timeouts.");
        long startTimeout = System.currentTimeMillis();
        Measurement.calibrateTimeouts(workerTemplateGenerator.getEngines(), dockerMachine, systemResources);
        timeout = timeout + System.currentTimeMillis() - startTimeout;
    }

    /**
     *
     * The method processes the the workerTemplates.
     *
     * @param workerTemplates The workerTemplates to process.
     */
    private void work(ArrayList<WorkerTemplate> workerTemplates) {

        List<WorkerTemplate> ownTemplates = splitTemplates(workerTemplates, tcpMessageService.getNumbers());

        LOGGER.info("Start the spawner.");
        long startExecution = System.currentTimeMillis();
        Spawner spawner = new Spawner(dockerMachine, ownTemplates, containerConfiguration, number);
        List<Container> containers = spawner.start();
        execution = execution + System.currentTimeMillis() - startExecution;

        LOGGER.info("Aggregation of the results.");
        Aggregator aggregator = new Aggregator(dockerMachine, containers);
        aggregator.start();

        tcpMessageService.sendReadyMessage(true);

        LOGGER.info("Wait for the clients.");
        boolean isWaiting = true;
        while (isWaiting) {
            isWaiting = !tcpMessageService.clientsAreReady();

        }
    }


    /**
     *
     * The method splits the workerTemplates.
     *
     * @param workerTemplates The workerTemplates to split.
     * @param numbers The number of containers, which can run parallel on this system.
     * @return The workerTemplates for this system.
     */
    private List<WorkerTemplate> splitTemplates(ArrayList<WorkerTemplate> workerTemplates, HashMap<ConnectionService, Integer> numbers) {
        LOGGER.info("Split templates.");
        int sumContainer = number;

        boolean isWaiting = true;
        while (isWaiting) {
            if (numbers.size() == tcpMessageService.getClientNumber()) {
                isWaiting = false;
            }
        }

        for (Integer number : numbers.values()) {
            sumContainer = sumContainer + number;
        }
        LOGGER.info("The hole system executes " + sumContainer + " container parallel.");

        int start = 0;
        HashMap<ConnectionService, List<WorkerTemplate>> templates = new HashMap<>();
        for (ConnectionService connectionService : numbers.keySet()) {
            int test = numbers.get(connectionService);
            Double endValue = workerTemplates.size() * ((double) test / (double) sumContainer);
            templates.put(connectionService, new ArrayList<>(workerTemplates.subList(start, endValue.intValue())));
            start = endValue.intValue();
        }
        tcpMessageService.sendWorkerTemplateMessage(templates);
        return new ArrayList<>(workerTemplates.subList(start, workerTemplates.size()));
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
            startBetsy = System.currentTimeMillis();
            Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
            endBetsy = System.currentTimeMillis();
            engines.forEach(e -> Images.buildEngine(dockerMachine, Paths.get("docker/image/engine").toAbsolutePath(), e.getName()));
            endEngines = System.currentTimeMillis();
        }
        return dockerMachine;
    }
}
