package betsy

import betsy.virtual.host.exceptions.ConfigurationException
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation

class Configuration {

    public static ConfigObject config = new ConfigSlurper().parse(new File("Config.groovy").toURI().toURL())

    /**
     * Get the value of the given key.
     *
     * @param key
     *            Key in config to get value for
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
     * @param key
     *            Key in config to get value for
     * @return Value assigned to the key, or default value if key is not set
     */
    public static Boolean getValueAsBoolean(final String key) {
        return DefaultTypeTransformation.castToBoolean(getValue(key));
    }

    /**
     * Get the value of the given key and cast it as Integer.
     *
     * @param key
     *            Key in config to get value for
     * @return Value assigned to the key, or default value if key is not set
     */
    public static Integer getValueAsInteger(final String key) {
        return Integer.parseInt(getValueAsString(key));
    }

    /**
     * Get the value of the given key and cast it as String.
     *
     * @param key
     *            Key in config to get value for
     * @return Value assigned to the key, or default value if key is not set
     */
    public static String getValueAsString(final String key) {
        return getValue(key).toString();
    }

    public static void setValue(final String key, final Object value) {
        config.put(key, value);
    }

    public static void assertDirectory(String key, String message) {
        String value = getValueAsString(key);

        if (!new File(value).isDirectory()) {
            throw new ConfigurationException("Found [" + value + "] for key [" + key + "] " + message);
        }
    }

}
