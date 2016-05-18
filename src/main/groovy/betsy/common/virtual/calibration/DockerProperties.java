package betsy.common.virtual.calibration;

import betsy.common.virtual.cbetsy.DockerEngine;
import betsy.common.virtual.cbetsy.WorkerTemplate;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          <p>
 *          This class contains the method to interact with the properties.
 */
public class DockerProperties {

    private static final Logger LOGGER = Logger.getLogger(DockerProperties.class);

    /**
     * This method reads the values for the given workerTemplates form the properties.
     *
     * @param propertiesPath  The file of the properties.
     * @param workerTemplates The {@link ArrayList} with the workerTemplates.
     * @return A {@link ArrayList} with the workerTemplates and the loaded values from the properties.
     */
    public static ArrayList<WorkerTemplate> readWorkerTemplates(Path propertiesPath, ArrayList<WorkerTemplate> workerTemplates) {
        Objects.requireNonNull(propertiesPath, "The propertiesFile can't be null.");
        Objects.requireNonNull(workerTemplates, "The workerTemplates can't be null.");
        if (Files.isReadable(propertiesPath)) {
            try (Reader reader = new FileReader(propertiesPath.toFile());){
                Properties properties = new Properties();
                properties.load(reader);

                for (WorkerTemplate workerTemplate : workerTemplates) {
                    String time = properties.getProperty(workerTemplate.getDockerEngine().getName() + ".time");
                    String memory = properties.getProperty(workerTemplate.getDockerEngine().getName() + ".memory");
                    if (memory != null) {
                        try {
                            workerTemplate.getDockerEngine().setTime(new Long(time));
                            workerTemplate.getDockerEngine().setMemory(new Integer(memory));
                        } catch (NumberFormatException e) {
                            LOGGER.info("The workerTemplate with the key " + workerTemplate.getID() + " was not read from the properties.");
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.info("Couldn't read the workerTemplate properties.");
            }
        } else {
            LOGGER.info("The file " + propertiesPath.toString() + " is not readable.");
        }
        return workerTemplates;
    }

    /**
     * This method reads the values for the given engines form the properties.
     *
     * @param propertiesPath The file of the properties.
     * @param engines        The {@link HashSet} with the engines.
     * @return A {@link HashSet} with the engines and the loaded values from the properties.
     */
    public static HashSet<DockerEngine> readEngines(Path propertiesPath, HashSet<DockerEngine> engines) {
        Objects.requireNonNull(propertiesPath, "The propertiesFile can't be null.");
        Objects.requireNonNull(engines, "The engines can't be null.");
        if (Files.isReadable(propertiesPath)) {
            try (Reader reader = new FileReader(propertiesPath.toFile())) {
                Properties properties = new Properties();
                properties.load(reader);

                for (DockerEngine engine : engines) {
                    String time = properties.getProperty(engine.getName() + ".time");
                    String memory = properties.getProperty(engine.getName() + ".memory");
                    if (memory != null) {
                        try {
                            engine.setTime(new Long(time));
                            engine.setMemory(new Integer(memory));
                        } catch (NumberFormatException e) {
                            LOGGER.info("The engine with the name " + engine.getName() + " was not read from the properties.");
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.info("Couldn't read the worker.properties.");
            }
        } else {
            LOGGER.info("The file " + propertiesPath.toString() + " is not readable.");
        }
        return engines;
    }

    /**
     * This method writes the values of the given list to the property file.
     *
     * @param propertiesPath The path were the properties should be saved.
     * @param engines        The engines with the values to write.
     */
    public static void writeEngines(Path propertiesPath, HashSet<DockerEngine> engines) {
        Objects.requireNonNull(propertiesPath, "The propertiesFile can't be null.");
        Objects.requireNonNull(engines, "The timeouts can't be null.");
        Writer writer = null;
        try {
            Properties properties;
            if (Files.isReadable(propertiesPath)) {
                Reader reader = new FileReader(propertiesPath.toFile());
                properties = new Properties();
                properties.load(reader);
                reader.close();
            } else {
                properties = new Properties();
            }
            writer = new FileWriter(propertiesPath.toFile());
            for (DockerEngine engine : engines) {
                properties.setProperty(engine.getName() + ".time", String.valueOf(engine.getTime()));
                properties.setProperty(engine.getName() + ".memory", String.valueOf(engine.getMemory()));
            }
            properties.store(writer, "worker.properties");
            writer.close();
        } catch (IOException e) {
            LOGGER.info("Couldn't store the properties.");
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (Exception e) {
                LOGGER.info("Couldn't close worker.properties.");
            }
        }
    }
}
