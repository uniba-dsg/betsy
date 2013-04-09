package de.uniba.wiai.dsg.betsy.virtual.host;

import java.io.File;

import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.ConfigurationException;

//TODO Javadoc
public class VBoxConfiguration {

	private final Configuration config = Configuration.getInstance();

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

	public File getVboxDir() {
		String vboxPath = config.getValueAsString("virtualisation.vbox.path");
		return new File(vboxPath);
	}

	public File getVBoxWebSrv() throws ConfigurationException {
		String vboxwebsrvPath = config
				.getValueAsString("virtualisation.vbox.vboxwebsrv");

		return new File(getVboxDir(), vboxwebsrvPath);
	}

	public File getVBoxManage() throws ConfigurationException {
		String vboxManagePath = config
				.getValueAsString("virtualisation.vbox.vboxmanage");
		return new File(getVboxDir(), vboxManagePath);
	}

}
