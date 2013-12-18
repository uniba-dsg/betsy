package betsy;

import betsy.virtual.host.exceptions.ConfigurationException;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration {
    /**
     * Get the value of the given key.
     *
     * @param key Key in config to get value for
     * @return Value assigned to the key, or null if key is not set
     */
    public static Object getValue(final String key) {
        Object result = config.toProperties().getProperty(key);

        if (result == null) {
            throw new ConfigurationException("No value found for key [" + key + "]");
        }


        return result;
    }

    /**
     * Get the value of the given key and cast it as boolean.
     *
     * @param key Key in config to get value for
     * @return Value assigned to the key, or default value if key is not set
     */
    public static Boolean getValueAsBoolean(final String key) {
        return Boolean.valueOf(getValueAsString(key));
    }

    /**
     * Get the value of the given key and cast it as Integer.
     *
     * @param key Key in config to get value for
     * @return Value assigned to the key, or default value if key is not set
     */
    public static Integer getValueAsInteger(final String key) {
        return Integer.parseInt(getValueAsString(key));
    }

    /**
     * Get the value of the given key and cast it as String.
     *
     * @param key Key in config to get value for
     * @return Value assigned to the key, or default value if key is not set
     */
    public static String getValueAsString(final String key) {
        return getValue(key).toString();
    }

    public static String get(final String key) {
        return getValueAsString(key);
    }

    public static Path getPath(final String key) {
        return Paths.get(get(key));
    }

    public static void set(final String key, final Object value) {
        setValue(key, value);
    }

    public static void setValue(final String key, final Object value) {
        config.put(key, value);
    }

    public static void assertDirectory(String key, String message) {
        Path value = getPath(key);

        if (!Files.isDirectory(value)) {
            throw new ConfigurationException("Found [" + value + "] for key [" + key + "] " + message);
        }

    }

    static {
        try {
            config = new ConfigSlurper().parse(new File("Config.groovy").toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException("something went wrong", e);
        }
    }

    private static ConfigObject config;
}
