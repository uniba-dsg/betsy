package de.uniba.wiai.dsg.betsy.virtual.host;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.virtualbox_4_2.CleanupMode;
import org.virtualbox_4_2.IAppliance;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMedium;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.LockType;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.vm.VirtualMachineNotFoundException;

/**
 * The {@link VirtualBoxController} establishes the connection between betsy and
 * VirtualBox. It can be used to resolve machines, import or delete them.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualBoxController {

	public static final String BETSY_VBOX_GROUP = "/betsy-engines";

	private final Logger log = Logger.getLogger(getClass());
	private final Configuration config = Configuration.getInstance();
	private final Map<String, VirtualMachine> virtualMachines = new HashMap<>();

	private final VirtualBoxManager vbManager;

	private IVirtualBox vBox;
	private VirtualBoxImporter vBoxImporter;

	public VirtualBoxController() {
		this.vbManager = VirtualBoxManager.createInstance(null);
	}

	/**
	 * Initialize the controller and connect to the VBoxWebSrv.
	 */
	public void init() {
		String host = config.getValueAsString("virtualisation.vboxwebsrv.host",
				"http://127.0.0.1");
		String port = config.getValueAsString("virtualisation.vboxwebsrv.port",
				"18083");
		String username = config.getValueAsString(
				"virtualisation.vboxwebsrv.user", "user");
		String password = config.getValueAsString(
				"virtualisation.vboxwebsrv.password", "password");

		try {
			this.vbManager.connect(host + ":" + port, username, password);
		} catch (org.virtualbox_4_2.VBoxException exception) {
			if (exception.getMessage().contains(
					"reasonText argument for createFault was passed NULL")) {
				log.warn("Connecting to vboxWebSrv failed, trying to deactivate websrvauthlibrary...");
				// try to switch the auth mode of VirtualBox
				String vbpath = config
						.getValueAsString("virtualisation.vbox.vboxmanage");
				Runtime r = Runtime.getRuntime();
				String cmd[] = { vbpath, "setproperty", "websrvauthlibrary",
						"null" };
				try {
					r.exec(cmd);
					log.info("... set VirtualBox websrvauthlibrary to 'null'");
				} catch (Exception e2) {
					log.warn("... couldn't null VirtualBox websrvauthlibrary:",
							e2);
				}

				this.vbManager.connect(host + ":" + port, username, password);
				log.info("Conneting to vboxWebSrv succeeded!");
			} else if (exception.getMessage().equals(
					"HTTP transport error: "
							+ "java.net.ConnectException: Connection refused")) {
				log.error("VBoxWebSrv does not seem to be running on the specified address!");
				throw exception;
			} else {
				// unknown exception, can't solve situation
				log.error("Unknown exception while connecting to vboxWebSrv");
				throw exception;
			}
		}

		this.vBox = vbManager.getVBox();
		log.debug(String.format("Using VirtualBox version '%s'",
				vBox.getVersion()));

		// no delay, continue with network usage immediately
		this.setLinkUpDelay(0);

		this.vBoxImporter = new VirtualBoxImporter(this.vBox);
	}

	/**
	 * Check if VirtualBox contains a virtual machine with the given name inside
	 * the group 'betsy-engines'.
	 * 
	 * @param vmName
	 *            name of the VM to search
	 * @return true if VirtualBox contains a name with the name
	 */
	public boolean containsMachine(final String vmName) {
		if (StringUtils.isBlank(vmName)) {
			throw new IllegalArgumentException(
					"vmName must not be null or empty");
		}

		List<String> groups = new LinkedList<>();
		groups.add(BETSY_VBOX_GROUP);
		List<IMachine> machines = vBox.getMachinesByGroups(groups);

		if (machines.isEmpty()) {
			log.info("VirtualBox does not contain any machines yet.");
			return false;
		}

		for (IMachine vm : machines) {
			if (vm.getName().equals(vmName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Import the Engine's virtualMachine from the given file.
	 * 
	 * @param vmName
	 *            desired name of the virtualMachine
	 * @param engineName
	 *            name of the engine the new VM belongs to
	 * @param importFile
	 *            file of the appliance to import
	 */
	public void importVirtualMachine(final String vmName,
			final String engineName, final File importFile) {

		if (StringUtils.isBlank(vmName)) {
			throw new IllegalArgumentException(
					"The name of the vm to import must not be null or empty");
		}
		if (StringUtils.isBlank(engineName)) {
			throw new IllegalArgumentException(
					"The name of the engine to import must not be null or empty");
		}
		if (importFile == null) {
			throw new IllegalArgumentException(
					"The file to import must not be null");
		}

		IMachine importedVm = null;
		ISession session = null;
		try {
			IAppliance appliance = vBoxImporter.importAppliance(importFile);

			// by definition the appliance container could contain several
			// separated machines which must be imported each at it's own.
			for (String uuid : appliance.getMachines()) {
				importedVm = vBox.findMachine(uuid);

				// acquire session lock
				session = vbManager.getSessionObject();
				importedVm.lockMachine(session, LockType.Write);
				IMachine lockedVM = session.getMachine();

				vBoxImporter
						.adjustMachineSettings(lockedVM, vmName, engineName);

				try {
					session.unlockMachine();
					session = null;
				} catch (VBoxException exception) {
					// ignore if was not locked
					log.debug("Failed to unlock session after import");
				}
			} // END FOR ITERATION
		} catch (VBoxException exception) {
			// session must be unlocked for deleting the vm
			if (session != null) {
				try {
					session.unlockMachine();
				} catch (VBoxException exception2) {
					log.debug("Failed to unlock session after import exception");
					// ignore if was not locked
				}
			}
			if (importedVm != null) {
				log.debug("Exception during import, delete VM again.");
				// Error --> delete VM again
				this.deleteMachine(importedVm);
			}
			log.warn("Unexpected import exception:", exception);
		}
	}

	/**
	 * Get the {@link VirtualMachine} of betsy with the given name.
	 * 
	 * @param name
	 *            name of the VirtualMachine to get
	 * @return VirtualMachine with the searched name
	 * @throws VirtualMachineNotFoundException
	 *             thrown if there is no VirtualMachine with this name
	 */
	public VirtualMachine getVirtualMachine(final String name)
			throws VirtualMachineNotFoundException {
		if (virtualMachines.containsKey(name)) {
			return virtualMachines.get(name);
		} else {
			VirtualMachine vm = new VirtualMachine(vbManager, getMachine(name));
			virtualMachines.put(name, vm);
			return vm;
		}
	}

	/**
	 * Get the {@link IMachine} of VirtualBox with the given name.
	 * 
	 * @param name
	 *            name of the IMachine to get
	 * @return IMachine with the searched name
	 * @throws VirtualMachineNotFoundException
	 *             thrown if there is no IMachine with this name
	 */
	public IMachine getMachine(final String name)
			throws VirtualMachineNotFoundException {
		List<String> groups = new LinkedList<>();
		groups.add(BETSY_VBOX_GROUP);
		List<IMachine> machines = vBox.getMachinesByGroups(groups);
		for (IMachine machine : machines) {
			if (machine.getName().equals(name)) {
				return machine;
			}
		}

		throw new VirtualMachineNotFoundException("VirtualMachine with name '"
				+ name + "' could not be found in betsy's VirtualBox group.");
	}

	/**
	 * Get the directory where VirtualBox stores it's VirtualMachine files.
	 * 
	 * @return directory where VirtualBox stores it's VirtualMachine files.
	 */
	public File getVBoxVirtualMachineFolder() {
		// get default vm folder
		return new File(vBox.getSystemProperties().getDefaultMachineFolder());
	}

	private void deleteMachine(final IMachine machine) {
		File logFolder = new File(machine.getLogFolder());
		logFolder.delete();

		List<IMedium> removableMediums = machine.unregister(CleanupMode.Full);
		machine.delete(removableMediums);
	}

	/**
	 * The LinkUpDelay specifies how many milliseconds the network adapter of
	 * the guest machine remains silent until he resumes his work stored in his
	 * networkstack.
	 * 
	 * @param milliSeconds
	 *            timeout to set in ms
	 */
	private void setLinkUpDelay(int milliSeconds) {
		if (Integer.parseInt(vBox.getExtraData("VBoxInternal/Devices/e1000/0/"
				+ "Config/LinkUpDelay")) != milliSeconds) {

			log.info("Disabling LinkUpDelay for this VirtualBox instance...");
			vBox.setExtraData(
					"VBoxInternal/Devices/e1000/0/Config/LinkUpDelay",
					Integer.toString(milliSeconds));
		}
	}

}
