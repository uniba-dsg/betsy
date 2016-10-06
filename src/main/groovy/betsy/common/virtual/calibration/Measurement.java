package betsy.common.virtual.calibration;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.Properties;
import betsy.common.timeouts.timeout.Timeout;
import betsy.common.timeouts.timeout.TimeoutRepository;
import betsy.common.virtual.cbetsy.DockerEngine;
import betsy.common.virtual.cbetsy.ResourceConfiguration;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.Containers;
import betsy.common.virtual.docker.Tasks;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
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
     * @param resourceConfiguration The resourceConfiguration of the system.
     * @return Returns false, if some timeout calibration fails for four time.
     */
    public static boolean calibrateTimeouts(HashSet<DockerEngine> engines, ResourceConfiguration resourceConfiguration) {
        Objects.requireNonNull(engines, "The engines can't be null.");
        Objects.requireNonNull(resourceConfiguration, "The resourceConfiguration can't be null.");
        long count = engines.stream().filter(k -> !calibrateTimeout(k, resourceConfiguration, 0, 1)).count();
        return count == 0;
    }

    /**
     * Ths method calibrate the the timeouts for the given engine.
     *
     * @param engine                The engine to calibrate.
     * @param resourceConfiguration The resourceConfiguration of the system.
     * @param counter               The counter of the attempts.
     * @param multiplier            The multiplier, which increases the timeouts.
     * @return Returns false, if the calibration failed.
     */
    private static boolean calibrateTimeout(DockerEngine engine, ResourceConfiguration resourceConfiguration, int counter, double multiplier) {
        Objects.requireNonNull(engine, "The engine can't be null.");
        Objects.requireNonNull(resourceConfiguration, "The resourceConfiguration can't be null.");
        Path timeout = docker.resolve("timeouts");
        FileTasks.mkdirs(timeout);

        Path enginePath = timeout.resolve(engine.getName().replace("_", "")).toAbsolutePath();
        FileTasks.mkdirs(enginePath);
        Path engineFilePath = enginePath.resolve("timeout.properties").toAbsolutePath();
        if (!engineFilePath.toFile().exists()) {
            if (counter < 4) {
                FileTasks.mkdirs(enginePath);
                Container container;

                //Remove existing container
                Optional<Container> existingContainer = Optional.ofNullable(Containers.getAll().get("timeoutCalibration_" + engine.getName().replace("_", "")));
                if (existingContainer.isPresent()) {
                    Containers.remove(existingContainer.get());
                }

                //Calculate container configuration
                int number = Measurement.calculateContainerNumber(resourceConfiguration, engine.getMemory());
                if (number <= 0) {
                    LOGGER.info("The system has not enough memory for this engines.");
                    return false;
                }
                ResourceConfiguration containerConfiguration = Measurement.calculateResources(resourceConfiguration, number);

                //Create Container
                if (engine.getTypeOfEngine() == DockerEngine.TypeOfEngine.BPEL) {
                    container = Containers.create("timeoutCalibration_" + engine.getName().replace("_", ""), engine.getName().replace("_", ""), containerConfiguration.getMemory(), containerConfiguration.getHddSpeed(), "calibrate", "bpel", engine.getName(), "sequence");
                } else {
                    container = Containers.create("timeoutCalibration_" + engine.getName().replace("_", ""), engine.getName().replace("_", ""), containerConfiguration.getMemory(), containerConfiguration.getHddSpeed(), "calibrate", "bpmn", engine.getName(), "sequenceFlow");
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
                    calibrateTimeout(engine, resourceConfiguration, ++counter, 1.25);
                }
                Containers.remove(container);
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
     * @param engines       The engines to measure.
     */
    public static HashSet<DockerEngine> measureMemoriesAndTimes(HashSet<DockerEngine> engines) {
        Objects.requireNonNull(engines, "The engines can't be null.");
        Objects.requireNonNull("The dockerMachine can't be null.");
        engines.forEach(k -> {
            if(k.getMemory() == 0 || k.getTime() == 0){
                measureMemoryAndTime(k);
            }
        });
        DockerProperties.writeEngines(docker.resolve("worker.properties"), engines);
        return  engines;
    }


    /**
     * Measures the duration and the peak memory usage during the execution of the sequence process in case of a BPELEngine
     * or the sequenceFLow process in case of a BPMNEngine on an engine.
     *
     * @param engine        The engine for the measurement.
     * @return Returns the dockerEngine.
     */
    private static DockerEngine measureMemoryAndTime(DockerEngine engine) {
        Objects.requireNonNull(engine, "The engine can't be null.");
        Optional<Container> existingContainer = Optional.ofNullable(Containers.getAll().get("calibration_" + engine.getName().replace("_", "")));
        if (existingContainer.isPresent()) {
            Containers.remove(existingContainer.get());
        }
        Container container;
        if (engine.getTypeOfEngine() == DockerEngine.TypeOfEngine.BPEL) {
            container = Containers.create("calibration_" + engine.getName().replace("_", ""), engine.getName().replace("_", ""), "sh", "betsy", "bpel", engine.getName(), "sequence");
        } else {
            container = Containers.create("calibration_" + engine.getName().replace("_", ""), engine.getName().replace("_", "").replace("_", ""), "sh", "betsy", "bpmn", engine.getName(), "sequenceFlow");
        }
        long start;
        long end = 0;
        Double memory = 0.0;

        int position = 0;
        Scanner scanner = Tasks.doDockerTaskWithOutput("stats");
        start = System.currentTimeMillis();
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
        Containers.remove(container);
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
        Double writeSpeed = testWriteSpeed(filePath, size);
        Double readSpeed = testReadSpeed(filePath, size);
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
        int freeMemory = 0;
        int hdd = 0;

        if (System.getProperty("os.name").contains("Windows")) {
            hdd = Measurement.measureHDDSpeed(docker.resolve("hdd_test.txt"), 1000);

            PowerShell powerShell = null;
            try {
                //Creates PowerShell session
                powerShell = PowerShell.openSession();
                //Increase timeout to give enough time to the script to finish
                Map<String, String> config = new HashMap<String, String>();
                config.put("maxWait", "80000");

                //Execute script
                PowerShellResponse response = powerShell.configuration(config).executeCommand("Get-VM MobyLinuxVM");
                String[] lines =  response.getCommandOutput().split("\n");

                int headLine = 0;
                int firstIndexOfMemory = 0;
                for(int i = 0; i < lines.length; i++){
                    if(headLine + 2 == i){
                        freeMemory = Integer.valueOf(lines[i].substring(firstIndexOfMemory, firstIndexOfMemory + "MemoryAssigned".length()).trim());
                    }
                    if(lines[i].contains("MemoryAssigned")){
                        firstIndexOfMemory = lines[i].indexOf("MemoryAssigned");
                        headLine = i;
                    }
                }

            } catch(PowerShellNotAvailableException ex) {
                LOGGER.info("Can't execute windows powershell command.");
            } finally {
                //Always close PowerShell session to free resources.
                if (powerShell != null)
                    powerShell.close();
            }

        } else {
            ProcessBuilder builder = new ProcessBuilder("free");
            Process process = null;
            try {
                process = builder.start();
            } catch (IOException e) {
                LOGGER.info("Cant' read free memory.");
            }
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
            freeMemory = 0;
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                if (nextLine.contains("Speicher")) {
                    String[] parts = nextLine.split("\\s+");
                    freeMemory = (new Integer(parts[1]) - new Integer(parts[2])) / 1000;
                }
            }
            LOGGER.info("Free memory: " + freeMemory  + "mb");
            hdd = (Measurement.measureHDDSpeed(docker.resolve("hdd_test.txt"), 1000)) / 10;
            LOGGER.info("HDD speed: " + hdd + "mb/s");
        }
        return new ResourceConfiguration(freeMemory , hdd);
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
