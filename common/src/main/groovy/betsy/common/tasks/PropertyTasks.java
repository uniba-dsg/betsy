package betsy.common.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PropertyTasks {

    public static void setPropertyInPropertiesFile(Path propertiesFile, String key, String value) {
        Properties properties = new Properties();

        // read
        try (BufferedReader reader = Files.newBufferedReader(propertiesFile)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load property file " + propertiesFile, e);
        }

        // modify
        properties.setProperty(key, value);

        // write
        try (BufferedWriter writer = Files.newBufferedWriter(propertiesFile)) {
            properties.store(writer, null);
        } catch (IOException e) {
            throw new IllegalStateException("Could not store property file " + propertiesFile, e);
        }
    }
}
