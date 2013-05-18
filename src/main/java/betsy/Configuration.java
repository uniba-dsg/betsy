package betsy;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

/**
 * Utility class to read the configuration settings.<br>
 * Values can be set in the 'Config.groovy' file located in the project's root.<br>
 * <br>
 * Config files can be included in two ways, listed with decreasing priority:<br>
 * <ul>
 * <li>External: Config.groovy must be in the project's root or in the same
 * folder as the .jar file</li>
 * <li>Internal: Config.groovy must be on the first level inside the .jar file</li>
 * </ul>
 * <br>
 * If neither internal or external config files are found, an empty Config
 * Object will be used.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public final class Configuration {

	private static final Logger log = Logger.getLogger(Configuration.class);
	private static final Configuration instance = new Configuration();

	private Map<Object, Object> configMap = new HashMap<>();

	private Configuration() {
		try {
			URL configURL = null;

			// 1st: check for external Config.groovy
			File external = new File("Config.groovy");
			if (external.isFile()) {
				configURL = external.toURI().toURL();
				log.info("Loading external config");
			} else {
				// 2nd: check for internal Config.groovy
				URL internal = this.getClass().getResource("/Config.groovy");
				if (internal != null) {
					configURL = internal;
					log.info("Loading internal config");
				}
			}

			if (configURL != null) {
				ConfigObject co = new ConfigSlurper().parse(configURL);
				Map<?, ?> rawMap = co.flatten();
				Set<?> keys = rawMap.keySet();
				for (Object key : keys) {
					Object value = rawMap.get(key);
					this.configMap.put(key, value);
				}
				log.info("Config loaded!");
			} else {
				log.warn("Neither external or internal Config.groovy file "
						+ "found. Using empty config now!");
			}
		} catch (Exception exception) {
			log.error("Config file could not be read, using empty config now!",
					exception);
		}
	}

	/**
	 * Get the instance of the {@link Configuration}.
	 * 
	 * @return Class instance
	 */
	public static Configuration getInstance() {
		return instance;
	}

	/**
	 * Check if a given key is defined in the loaded Config.
	 * 
	 * @param key
	 *            Key to search in config
	 * @return true if key is found, else false
	 */
	public boolean isValueDefined(final String key) {
		return configMap.containsKey(key);
	}

	/**
	 * Get the value of the given key.
	 * 
	 * @param key
	 *            Key in config to get value for
	 * @return Value assigned to the key, or null if key is not set
	 */
	public Object getValue(final String key) {
		return getValue(key, null);
	}

	/**
	 * Get the value of the given key.
	 * 
	 * @param key
	 *            Key in config to get value for
	 * @return Value assigned to the key, or return default value if key was not
	 *         found
	 */
	public Object getValue(final String key, final Object defaultValue) {
		if (isValueDefined(key)) {
			return configMap.get(key);
		} else {
			return defaultValue;
		}
	}

	/**
	 * Get the value of the given key and cast it as boolean.
	 * 
	 * @param key
	 *            Key in config to get value for
	 * @return Value assigned to the key, or default value if key is not set
	 */
	public Boolean getValueAsBoolean(final String key,
			final Boolean defaultValue) {
		return DefaultTypeTransformation.castToBoolean(getValue(key,
				defaultValue));
	}

	/**
	 * Get the value of the given key and cast it as Integer.
	 * 
	 * @param key
	 *            Key in config to get value for
	 * @return Value assigned to the key, or default value if key is not set
	 */
	public Integer getValueAsInteger(final String key,
			final Integer defaultValue) {
		return DefaultTypeTransformation.castToNumber(
				getValue(key, defaultValue)).intValue();
	}

	/**
	 * Get the value of the given key and cast it as String.
	 * 
	 * @param key
	 *            Key in config to get value for
	 * @return Value assigned to the key, or null if key is not set
	 */
	public String getValueAsString(final String key) {
		return getValueAsString(key, null);
	}

	/**
	 * Get the value of the given key and cast it as String.
	 * 
	 * @param key
	 *            Key in config to get value for
	 * @return Value assigned to the key, or default value if key is not set
	 */
	public String getValueAsString(final String key, final String defaultValue) {
		return String.valueOf(getValue(key, defaultValue));
	}

	public void setValue(final String key, final Object value) {
		this.configMap.put(key, value);
	}

}