package betsy.common.virtual.calibration;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.Properties;
import betsy.common.timeouts.timeout.Timeout;
import betsy.common.timeouts.timeout.TimeoutRepository;
import betsy.common.virtual.cbetsy.DockerEngine;
import betsy.common.virtual.cbetsy.ResourceConfiguration;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.Containers;
import betsy.common.virtual.docker.DockerMachine;
import betsy.common.virtual.docker.Tasks;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Measurement {

    private static final Logger LOGGER = Logger.getLogger(Measurement.class);
    private static Path docker = Paths.get(get("docker.dir")).toAbsolutePath();

    /**
     * Ths method calibrate the the timeouts for the given engines.
     *
     * @param engines               The engines to calibrate.
     * @param dockerMachine         The dockerMachine to execute on.
     * @param resourceConfiguration The resourceConfiguration of the system.
     * @return Returns false, if some timeout calibration fails for four time.
     */
    public static boolean calibrateTimeouts(HashSet<DockerEngine> engines, DockerMachine dockerMachine, ResourceConfiguration resourceConfiguration) {
        Objects.requireNonNull(engines, "The engines can't be null.");
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(resourceConfiguration, "The resourceConfiguration can't be null.");
        long count = engines.stream().filter(k -> !calibrateTimeout(k, dockerMachine, resourceConfiguration, 0, 1)).count();
        return count <= 0;
    }

    /**
     * Ths method calibrate the the timeouts for the given engine.
     *
     * @param engine                The engine to calibrate.
     * @param dockerMachine         The dockerMachine to execute on.
     * @param resourceConfiguration The resourceConfiguration of the system.
     * @param counter               The counter of the attempts.
     * @param multiplier            The multiplier, which increases the timeouts.
     * @return Returns false, if the calibration failed.
     */
    private static boolean calibrateTimeout(DockerEngine engine, DockerMachine dockerMachine, ResourceConfiguration resourceConfiguration, int counter, double multiplier) {
        Objects.requireNonNull(engine, "The engine can't be null.");
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(resourceConfiguration, "The resourceConfiguration can't be null.");
        Path timeout = docker.resolve("timeouts");
        FileTasks.mkdirs(timeout);

        Path enginePath = timeout.resolve(engine.getName().replace("_", "")).toAbsolutePath();
        Path engineFilePath = enginePath.resolve("timeout.properties").toAbsolutePath();
        if (!engineFilePath.toFile().exists()) {
            if (counter < 4) {
                FileTasks.mkdirs(enginePath);
                Container container;

                //Remove existing container
                Optional<Container> existingContainer = Optional.ofNullable(Containers.getAll(dockerMachine).get("timeoutCalibration_" + engine.getName().replace("_", "")));
                if (existingContainer.isPresent()) {
                    Containers.remove(dockerMachine, existingContainer.get());
                }

                //Calculate container configuration
                int number = Measurement.calculateContainerNumber(resourceConfiguration, engine.getMemory());
                if(number <= 0){
                    LOGGER.info("The system has not enough memory for this engines.");
                    return false;
                }
                ResourceConfiguration containerConfiguration = Measurement.calculateResources(resourceConfiguration, number);

                //Create Container
                if (engine.getTypeOfEngine() == DockerEngine.TypeOfEngine.BPEL) {
                    container = Containers.create(dockerMachine, "timeoutCalibration_" + engine.getName().replace("_", ""), engine.getName().replace("_", ""), containerConfiguration.getMemory(), containerConfiguration.getHddSpeed(), "calibrate", "bpel", engine.getName(), "sequence");
                } else {
                    container = Containers.create(dockerMachine, "timeoutCalibration_" + engine.getName().replace("_", ""), engine.getName().replace("_", ""), containerConfiguration.getMemory(), containerConfiguration.getHddSpeed(), "calibrate", "bpmn", engine.getName(), "sequenceFlow");
                }
                //Create timeout.properties and copy to container
                Path properties = timeout.resolve("timeout.properties");
                FileTasks.deleteFile(properties);
                HashMap<String, Timeout> timeouts = TimeoutRepository.getAllCalibrateable();
                timeouts.forEach((e, k) -> k.setValue(new Double(k.getTimeoutInMs() * multiplier).intValue()));
                Properties.write(properties, new ArrayList<>(timeouts.values()));
                container.copyToContainer(properties, Paths.get("/betsy"));

                container.start(true);
                container.exec("rm", "timeout.properties");
                Container.Status status = container.getStatus();

                //wait until the container stopped
                while (status == Container.Status.RUNNING) {
                    status = container.getStatus();
                }

                //Copy timeout.properties from container
                container.copyFromContainer(Paths.get("/betsy/timeout.properties"), enginePath);

                //if the file is not existing, the calibration wasn't successful.
                if (!engineFilePath.toFile().exists()) {
                    calibrateTimeout(engine, dockerMachine, resourceConfiguration, ++counter, 1.1);
                }
                Containers.remove(dockerMachine, container);
                return true;
            } else {
                LOGGER.info("The timeout for the engine " + engine + " couldn't be calibrated.");
                return false;
            }
        } else {
            LOGGER.info("The timeouts are calibrated.");
            return true;
        }
    }

    /**
     * @param dockerMachine The dockerMachine to execute on.
     * @param engines       The engines to measure.
     */
    public static void measureMemoriesAndTimes(DockerMachine dockerMachine, HashSet<DockerEngine> engines) {
        Objects.requireNonNull(engines, "The engines can't be null.");
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        engines.forEach(k -> measureMemoryAndTime(dockerMachine, k));
        DockerProperties.writeEngines(docker.resolve("worker.properties"), engines);
    }


    /**
     * Measures the duration and the peak memory usage during the execution of the sequence process in case of a BPELEngine
     * or the sequenceFLow process in case of a BPMNEngine on an engine.
     *
     * @param dockerMachine The dockerMachine for the measurement.
     * @param engine        The engine for the measurement.
     * @return Returns the dockerEngine.
     */
    private static DockerEngine measureMemoryAndTime(DockerMachine dockerMachine, DockerEngine engine) {
        Objects.requireNonNull(engine, "The engine can't be null.");
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Optional<Container> existingContainer = Optional.ofNullable(Containers.getAll(dockerMachine).get("calibration_" + engine.getName().replace("_", "")));
        if (existingContainer.isPresent()) {
            Containers.remove(dockerMachine, existingContainer.get());
        }
        Container container;
        if (engine.getTypeOfEngine() == DockerEngine.TypeOfEngine.BPEL) {
            container = Containers.create(dockerMachine, "calibration_" + engine.getName().replace("_", ""), engine.getName().replace("_", ""), "sh", "betsy", "bpel", engine.getName(), "sequence");
        } else {
            container = Containers.create(dockerMachine, "calibration_" + engine.getName().replace("_", ""), engine.getName().replace("_", "").replace("_", ""), "sh", "betsy", "bpmn", engine.getName(), "sequenceFlow");
        }
        long start;
        long end = 0;
        Double memory = 0.0;
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
        Containers.remove(dockerMachine, container);
        engine.setMemory(memory.intValue());
        engine.setTime(end - start);
        return engine;
    }


    /**
     * This method executes the evaluation of the hddSpeed.
     *
     * @param filePath The path of the testFile.
     * @param size     Tje size of the testFile.
     * @return * @return Returns the hddSpeed as {@link Double}.
     */
    private static int measureHDDSpeed(Path filePath, long size) {
        Objects.requireNonNull(filePath, "The filePath can't be null.");
        Double writeSpeed = testWriteSpeed(filePath, size) / 10;
        Double readSpeed = testReadSpeed(filePath, size) / 10;
        if (writeSpeed > readSpeed) {
            filePath.toFile().delete();
            return readSpeed.intValue();
        } else {
            filePath.toFile().delete();
            return writeSpeed.intValue();
        }
    }


    /**
     * This method test the writeSpeed of the hdd.
     *
     * @param filePath The path of the testFile.
     * @param size     The size of test file.
     * @return Returns the writeSpeed as {@link Double}.
     */
    private static double testWriteSpeed(Path filePath, long size) {
        Objects.requireNonNull(filePath, "The filePath can't be null.");
        byte[] oneMB = new byte[1024 * 1024];
        File file = filePath.toFile();
        BufferedOutputStream writer = null;
        long start = System.currentTimeMillis();
        try {
            writer = new BufferedOutputStream(new FileOutputStream(file), 1024 * 1024);
            for (int i = 0; i < size; i++) {
                writer.write(oneMB);
            }
            writer.flush();
        } catch (IOException e) {
            LOGGER.info("Can't test hdd write speed.");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.info("Can't test hdd write speed.");
                }
            }
        }
        long elapsed = System.currentTimeMillis() - start;
        double seconds = ((double) elapsed) / 1000L;
        return ((double) size) / seconds;
    }

    /**
     * This method test the readSpeed of the hdd.
     *
     * @param filePath The path of the testFile.
     * @param size     The size of test file.
     * @return Returns the readSpeed as {@link Double}.
     */
    private static double testReadSpeed(Path filePath, long size) {
        Objects.requireNonNull(filePath, "The filePath can't be null.");
        byte[] oneMB = new byte[1024 * 1024];
        File file = filePath.toFile();
        BufferedInputStream reader = null;
        long start = System.currentTimeMillis();
        try {
            reader = new BufferedInputStream(new FileInputStream(file), 1024 * 1024);
            for (int i = 0; i < size; i++) {
                reader.read(oneMB);
            }
        } catch (IOException e) {
            LOGGER.info("Can't test hdd read speed.");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.info("Can't test hdd read speed.");
                }
            }
        }
        long elapsed = System.currentTimeMillis() - start;
        double seconds = ((double) elapsed) / 1000L;
        return ((double) size) / seconds;
    }


    /**
     * This method measures the resourceConfiguration of the hole system.
     *
     * @return The resourceConfiguration of the system.
     */
    public static ResourceConfiguration measureResources() {
        ProcessBuilder builder = new ProcessBuilder("free");
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            LOGGER.info("Cant' read free memory.");
        }
        Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
        int freeMemory = 0;
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            if (nextLine.contains("Speicher")) {
                String[] parts = nextLine.split("\\s+");
                freeMemory = new Integer(parts[1]) - new Integer(parts[2]);
            }
        }
        LOGGER.info("Free memory: " + freeMemory / 1000 + "mb");
        int hdd = Measurement.measureHDDSpeed(docker.resolve("hdd_test.txt"), 1000);
        LOGGER.info("HDD speed: " + hdd + "mb/s");
        return new ResourceConfiguration(freeMemory / 1000, hdd);
    }


    /**
     * This method calculates the number of containers, which can be executed parallel.
     *
     * @param resourceConfiguration The resourceConfiguration of the system.
     * @param memory                The maximum memory usage of an used engine.
     * @return Returns the number of parallel executable containers.
     */
    public static int calculateContainerNumber(ResourceConfiguration resourceConfiguration, int memory) {
        Objects.requireNonNull(resourceConfiguration, "The resourceConfiguration can't be null.");
        if (memory > 0) {
            return resourceConfiguration.getMemory() / memory;
        } else {
            throw new IllegalArgumentException("The memory has to be greater than 0.");
        }
    }

    /**
     * This method calculates the resources, which are available for a single container.
     *
     * @param resourceConfiguration The resourceConfiguration of the system.
     * @param number                The number of parallel executable containers.
     * @return Returns the resourceConfiguration of the system.
     */
    public static ResourceConfiguration calculateResources(ResourceConfiguration resourceConfiguration, int number) {
        Objects.requireNonNull(resourceConfiguration, "The resourceConfiguration can't be null.");
        if (number > 0) {
            return new ResourceConfiguration(resourceConfiguration.getMemory() / number, resourceConfiguration.getHddSpeed() / number);
        } else {
            throw new IllegalArgumentException("The number has to be greater than 0.");
        }

    }


    /**
     * This method returns the maximum memory usage of all engines.
     *
     * @param engines The engines to evaluate.
     * @return The maximum memory usage as {@link Integer}.
     */
    public static Integer evaluateMaxMemory(HashSet<DockerEngine> engines) {
        Objects.requireNonNull(engines, "The engines can't be null.");
        List<Integer> memories = new ArrayList<>();
        engines.forEach(k -> memories.add(new Double(k.getMemory()).intValue()));
        return Collections.max(memories);
    }


}
