package betsy.common.virtual.calibration;

import betsy.common.virtual.WorkerTemplate;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class contains the method to interact with the properties.
 *
 */
public class Properties {

    private static final Logger LOGGER = Logger.getLogger(Properties.class);

    /**
     * This method reads the values for the given workerTemplates form the properties.
     *
     * @param propertiesPath    The file of the properties.
     * @param workerTemplates   The {@link TreeMap} with the workerTemplates.
     * @return                  A {@link List} with the timeouts and the loaded values from the properties.
     */
    public static ArrayList<WorkerTemplate> read(Path propertiesPath, ArrayList<WorkerTemplate> workerTemplates) {
        Objects.requireNonNull(propertiesPath, "The propertiesFile can't be null.");
        Objects.requireNonNull(workerTemplates, "The workerTemplates can't be null.");
        if (Files.isReadable(propertiesPath)) {
            Reader reader = null;
            try {
                reader = new FileReader(propertiesPath.toFile());
                java.util.Properties properties = new java.util.Properties();
                properties.load(reader);

                for (WorkerTemplate workerTemplate : workerTemplates) {
                    String time = properties.getProperty(workerTemplate.getEngineName() + ".time");
                    String memory = properties.getProperty(workerTemplate.getEngineName() + ".memory");
                    if (memory != null) {
                        try {
                            workerTemplate.setTime(new Long(time));
                            workerTemplate.setMemory(new Double(memory));
                        } catch (NumberFormatException e) {
                            LOGGER.info("The workerTemplate with the key " + workerTemplate.getID() + " was not read from the properties. The workerTemplate have to be an long.");
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.info("Couldn't read the workerTemplate properties.");
            } finally {
                assert reader != null;
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.info("Couldn't close the workerTemplate properties fileReader.");
                }
            }
        } else {
            LOGGER.info("The file " + propertiesPath.toString() + " is not readable.");
        }
        return workerTemplates;
    }

    /**
     * This method writes the values of the given arrays to the property file.
     *
     * @param propertiesPath The path were the properties should be saved.
     * @param values         The arrays consists of the engine name, the duration and the memory.
     */
    public static void write(Path propertiesPath, ArrayList<String[]> values) {
        Objects.requireNonNull(propertiesPath, "The propertiesFile can't be null.");
        Objects.requireNonNull(values, "The timeouts can't be null.");
        Writer writer = null;
        try {
            java.util.Properties properties;
            if (Files.isReadable(propertiesPath)) {
                Reader reader = new FileReader(propertiesPath.toFile());
                properties = new java.util.Properties();
                properties.load(reader);
                reader.close();
            } else {
                properties = new java.util.Properties();
            }
            writer = new FileWriter(propertiesPath.toFile());
            for (String[] value : values) {
                properties.setProperty(value[0] + ".time", value[1]);
                properties.setProperty(value[0] + ".memory", value[2]);
            }
            properties.store(writer, "WorkerTemplate_properties");
            writer.close();
        } catch (IOException e) {
            LOGGER.info("Couldn't store the properties ");
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (Exception e) {
                LOGGER.info("Couldn't close the workerTemplate properties fileReader.");
            }
        }
    }
}
