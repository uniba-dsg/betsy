package betsy.common.virtual.calibration;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.Properties;
import betsy.common.timeouts.timeout.Timeout;
import betsy.common.timeouts.timeout.TimeoutRepository;
import betsy.common.virtual.ParallelRunner;
import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.Containers;
import betsy.common.virtual.docker.DockerMachine;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Measurement {

    private static final Logger LOGGER = Logger.getLogger(ParallelRunner.class);

    /**
     * Ths method calibrate the the timeouts for the given workerTemplates respectively the used engines.
     *
     * @param engines The workerTemplates to calibrate.
     * @param dockerMachine   The dockerMachine to execute on.
     * @param memory          The maximum memory of a container.
     * @param cpuShares       The cpuShares of a container.
     * @param hddSpeed        The maximum hddSpeed of a container.
     */
    public static void calibrateTimeouts(HashMap<String, Boolean> engines, DockerMachine dockerMachine, int memory, int cpuShares, int hddSpeed) {
        engines.forEach((e, k) -> calibrateTimeout(e, k, dockerMachine, memory, cpuShares, hddSpeed, 0, 1));
    }

    /**
     * Ths method calibrate the the timeouts for the given workerTemplate respectively the used engine.
     *
     * @param engine The workerTemplate to calibrate.
     * @param dockerMachine  The dockerMachine to execute on.
     * @param memory         The maximum memory of a container.
     * @param cpuShares      The cpuShares of a container.
     * @param hddSpeed       The maximum hddSpeed of a container.
     * @param counter        The counter of the attempts.
     * @param multiplier     The multiplier, which increases the timeouts.
     */
    private static void calibrateTimeout(String engine, boolean isBPELEngine, DockerMachine dockerMachine, int memory, int cpuShares, int hddSpeed, int counter, double multiplier) {
        if (counter < 10) {
            FileTasks.mkdirs(Paths.get("docker").resolve(engine));
            Container container;
            if (isBPELEngine) {
                //Remove existing container
                Optional<Container> existingContainer = Optional.ofNullable(Containers.getAll(dockerMachine).get(engine.replace("_", "")));
                if (existingContainer.isPresent()) {
                    Containers.remove(dockerMachine, existingContainer.get());
                }
                //Create container
                container = Containers.create(dockerMachine, "timeoutCalibration_" + engine, engine.replace("_", ""), cpuShares, memory, hddSpeed, "calibrate", "bpel", engine, "sequence");
            } else {
                container = Containers.create(dockerMachine, "timeoutCalibration_" + engine, engine.replace("_", ""), cpuShares, memory, hddSpeed, "calibrate", "bpmn", engine, "sequenceFlow");
            }
            //Create timeout.properties and copy to container
            Path properties = Paths.get("timeout.properties");
            HashMap<String, Timeout> timeouts = TimeoutRepository.getAllCalibrateable();
            timeouts.forEach((e, k) -> k.setValue(new Double(k.getTimeoutInMs() * multiplier).intValue()));
            Properties.write(properties, new ArrayList<>(timeouts.values()));
            container.copyToContainer(properties, Paths.get("/betsy"));

            container.start(false);

            //Copy timeout.properties from container
            container.copyFromContainer(Paths.get("/betsy/timeout.properties"), Paths.get(engine));

            //if the file is not existing, the calibration wasn't successful.
            if (!Paths.get("docker").resolve(engine).resolve("timeout.properties").toFile().exists()) {
                calibrateTimeout(engine, isBPELEngine, dockerMachine, memory, cpuShares, hddSpeed, ++counter, 1.1);
            }
        } else {
            LOGGER.info("The timeouts couldn't be calibrated.");
            System.exit(0);
        }
    }


    /**
     * This method executes the evaluation of the hddSpeed.
     *
     * @param path The path of the testFile.
     * @param size Tje size of the testFile.
     * @return * @return Returns the hddSpeed as {@link Double}.
     */
    public static double execute(Path path, long size) {
        double writeSpeed = testWriteSpeed(path, size);
        double readSpeed = testReadSpeed(path, size);
        if (writeSpeed > readSpeed) {
            path.toFile().delete();
            return readSpeed;
        } else {
            path.toFile().delete();
            return writeSpeed;
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
        return calculateSpeed(size, elapsed);
    }

    /**
     * This method test the readSpeed of the hdd.
     *
     * @param filePath The path of the testFile.
     * @param size     The size of test file.
     * @return Returns the readSpeed as {@link Double}.
     */
    private static double testReadSpeed(Path filePath, long size) {
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
        return calculateSpeed(size, elapsed);
    }

    /**
     * This method calculated the speed for writing or reading based on time and size.
     *
     * @param size    The size of the files.
     * @param elapsed The duration for writing or reading the file.
     * @return Returns the calculated speed as {@link Double}.
     */
    private static double calculateSpeed(long size, long elapsed) {
        double seconds = ((double) elapsed) / 1000L;
        return ((double) size) / seconds;
    }

}
