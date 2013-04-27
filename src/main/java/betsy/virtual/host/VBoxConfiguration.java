package betsy.virtual.host;

import java.io.File;

import betsy.Configuration;
import betsy.virtual.host.exceptions.ConfigurationException;

/**
 * The {@link VBoxConfiguration} provides access to mandatory options required
 * by virtualized testing using VirtualBox.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VBoxConfiguration {

	private final Configuration config = Configuration.getInstance();

	/**
	 * Verify if all mandatory options are set.
	 * 
	 * @throws ConfigurationException
	 *             thrown if at least one config setting is missing or contains
	 *             invalid values
	 */
	public void verify() throws ConfigurationException {
		String vboxPath = config.getValueAsString("virtualisation.vbox.path");
		String vboxwebsrvPath = config
				.getValueAsString("virtualisation.vbox.vboxwebsrv");
		String vboxManagePath = config
				.getValueAsString("virtualisation.vbox.vboxmanage");

		if (vboxPath == null) {
			throw new ConfigurationException(
					"Please specify a path to the "
							+ "VirtualBox installation directory in the 'Config.groovy' "
							+ "file located in the project's root.");
		}
		if (vboxwebsrvPath == null) {
			throw new ConfigurationException("Please specify a path to the "
					+ "VBoxWebSrv executable in the 'Config.groovy' file "
					+ "located in the project's root.");
		}
		if (vboxManagePath == null) {
			throw new ConfigurationException("Please specify a path to the "
					+ "VBoxManage executable in the 'Config.groovy' file "
					+ "located in the project's root.");
		}

		File vboxDir = getVboxDir();
		File vboxWebSrv = getVBoxWebSrv();
		File vboxManage = getVBoxManage();

		if (!vboxDir.isDirectory()) {
			throw new ConfigurationException("The VirtualBox installation "
					+ "directory, specified in the 'Config.groovy', could not "
					+ "be found.");
		}
		if (!vboxWebSrv.isFile()) {
			throw new ConfigurationException("The VBoxWebSrv executable "
					+ ",specified in the 'Config.groovy', could not be found.");
		}
		if (!vboxManage.isFile()) {
			throw new ConfigurationException("The VBoxManage executable "
					+ ",specified in the 'Config.groovy', could not be found.");
		}
	}

	/**
	 * Get the File where VirtualBox is installed in.
	 * 
	 * @return directory file
	 */
	public File getVboxDir() {
		String vboxPath = config.getValueAsString("virtualisation.vbox.path");
		return new File(vboxPath);
	}

	/**
	 * Get the VirtualBox WebService File
	 * 
	 * @return file of VirtualBox WebService
	 */
	public File getVBoxWebSrv() {
		String vboxwebsrvPath = config
				.getValueAsString("virtualisation.vbox.vboxwebsrv");
		return new File(getVboxDir(), vboxwebsrvPath);
	}

	/**
	 * Get the VirtualBox VBoxManage File
	 * 
	 * @return file of VirtualBox VBoxManage
	 */
	public File getVBoxManage() {
		String vboxManagePath = config
				.getValueAsString("virtualisation.vbox.vboxmanage");
		return new File(getVboxDir(), vboxManagePath);
	}

}
