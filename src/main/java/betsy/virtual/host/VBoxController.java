package betsy.virtual.host;

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

import betsy.virtual.host.exceptions.vm.VirtualMachineNotFoundException;

/**
 * The {@link VBoxController} establishes the connection between betsy and
 * VirtualBox. It can be used to resolve machines, import or delete them and to
 * adapt some settings of VirtualBox.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VBoxController {

	public static final String BETSY_VBOX_GROUP = "/betsy-engines";
	private static final Logger log = Logger.getLogger(VBoxController.class);

	private final Map<String, VirtualMachine> virtualMachines = new HashMap<>();

	private IVirtualBox vBox;
	private VBoxApplianceImporter vBoxImporter;
	private VirtualBoxManager vBoxManager;

	public VBoxController() {
	}

	/**
	 * Initialize the controller and connect to the VirtualBox interface.
	 */
	public void init() {
		log.trace("Initializing VBoxController");
		VBoxConnector vBoxConn = VBoxConnector.getInstance();
		this.vBox = vBoxConn.connect();
		this.vBoxManager = vBoxConn.getVBoxManager();
		this.vBoxImporter = vBoxConn.getVBoxImporter();

		log.trace("VirtualBoxController initialized");
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
				session = vBoxManager.getSessionObject();
				importedVm.lockMachine(session, LockType.Write);
				IMachine lockedVM = session.getMachine();

				vBoxImporter.adjustApplianceSettings(lockedVM, vmName);

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
			VirtualMachine vm = new VirtualMachine(vBoxManager,
					getMachine(name));
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

}
