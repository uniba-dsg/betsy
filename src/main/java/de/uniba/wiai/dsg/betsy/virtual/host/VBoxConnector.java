package de.uniba.wiai.dsg.betsy.virtual.host;

import org.apache.log4j.Logger;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.VirtualBoxManager;

import de.uniba.wiai.dsg.betsy.Configuration;

/**
 * The {@link VBoxConnector} establishes the connection between betsy and
 * VirtualBox. It also provides objects that should be created only once per
 * connection, such as the {@link VirtualBoxManager} or the
 * {@link VBoxApplianceImporter}.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VBoxConnector {

	private static VBoxConnector instance = null;

	private final Configuration config = Configuration.getInstance();
	private final Logger log = Logger.getLogger(getClass());
	private final VirtualBoxManager vBoxManager;

	private IVirtualBox vBox = null;
	private VBoxApplianceImporter vBoxImporter = null;

	private boolean isConnected = false;

	private VBoxConnector() {
		this.vBoxManager = VirtualBoxManager.createInstance(null);
	}

	public static synchronized VBoxConnector getInstance() {
		if (instance == null) {
			instance = new VBoxConnector();
		}
		return instance;
	}

	private boolean isNotConnected() {
		return !isConnected;
	}

	public VirtualBoxManager getVirtualBoxManager() {
		return this.vBoxManager;
	}

	public VBoxApplianceImporter getVirtualBoxImporter() {
		return this.vBoxImporter;
	}

	/**
	 * Establish the connection to the VirtualBox web service of not already
	 * done.<br>
	 * Creates also other important objects as the {@link VirtualBoxManager} and
	 * a {@link VBoxApplianceImporter}.
	 * 
	 * @return VirtualBox interface object
	 */
	public IVirtualBox connect() {
		if (isNotConnected()) {
			String host = config.getValueAsString(
					"virtualisation.vboxwebsrv.host", "http://127.0.0.1");
			String port = config.getValueAsString(
					"virtualisation.vboxwebsrv.port", "18083");
			String username = config.getValueAsString(
					"virtualisation.vboxwebsrv.user", "user");
			String password = config.getValueAsString(
					"virtualisation.vboxwebsrv.password", "password");

			try {
				this.vBoxManager.connect(host + ":" + port, username, password);
				isConnected = true;

				vBox = vBoxManager.getVBox();
				log.debug(String.format("Using VirtualBox version '%s'",
						vBox.getVersion()));
				vBoxImporter = new VBoxApplianceImporter(vBox);
			} catch (org.virtualbox_4_2.VBoxException exception) {
				if (exception.getMessage().contains(
						"reasonText argument for "
								+ "createFault was passed NULL")) {
					log.error("Connecting to vboxWebSrv failed, please deactivate"
							+ " the websrvauthlibrary manually: 'VBoxManage.exe "
							+ "setproperty websrvauthlibrary null'");
					throw exception;
				} else if (exception.getMessage().equals(
						"HTTP transport error: java.net.ConnectException: "
								+ "Connection refused")) {
					log.error("VBoxWebSrv does not seem to be running on the specified address!");
					throw exception;
				} else {
					// unknown exception, can't solve situation
					log.error("Unknown exception while connecting to vboxWebSrv");
					throw exception;
				}
			}
		}
		return vBox;
	}

}
