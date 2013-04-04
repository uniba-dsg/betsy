package de.uniba.wiai.dsg.betsy.virtual.host;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.virtualbox_4_2.ChipsetType;
import org.virtualbox_4_2.CleanupMode;
import org.virtualbox_4_2.IAppliance;
import org.virtualbox_4_2.IHost;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMedium;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.ISharedFolder;
import org.virtualbox_4_2.ISystemProperties;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.LockType;
import org.virtualbox_4_2.NetworkAdapterType;
import org.virtualbox_4_2.NetworkAttachmentType;
import org.virtualbox_4_2.ProcessorFeature;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.vm.VirtualMachineException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.vm.VirtualMachineNotFoundException;

public class VirtualBoxController {

	public static final String BETSY_VBOX_GROUP = "/betsy-engines";

	private final Logger log = Logger.getLogger(getClass());
	private final Configuration config = Configuration.getInstance();
	private final VirtualBoxManager vbManager;
	private final IVirtualBox vBox;
	private final Map<String, VirtualMachine> virtualMachines = new HashMap<>();

	public VirtualBoxController() {
		String host = config.getValueAsString("virtualisation.vboxwebsrv.host",
				"http://127.0.0.1");
		String port = config.getValueAsString("virtualisation.vboxwebsrv.port",
				"18083");
		String username = config.getValueAsString(
				"virtualisation.vboxwebsrv.user", "user");
		String password = config.getValueAsString(
				"virtualisation.vboxwebsrv.password", "password");

		this.vbManager = VirtualBoxManager.createInstance(null);
		try {
			this.vbManager.connect(host + ":" + port, username, password);
		} catch (org.virtualbox_4_2.VBoxException exception) {
			if (exception.getMessage().equals(
					"reasonText argument for createFault was passed NULL")) {
				log.warn("Connecting to vboxWebSrv failed, trying to deactivate websrvauthlibrary...");
				// try to switch the auth mode of VirtualBox
				String vbpath = config
						.getValueAsString("virtualisation.vboxmanage");
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
			} else if (exception
					.getMessage()
					.equals("HTTP transport error: java.net.ConnectException: Connection refused")) {
				log.error("VBoxWebSrv does not seem to be running on the specified address!");
				throw exception;
			} else {
				// unknown exception, can't solve situation
				log.error("Unknown exception while connecting to vboxWebSrv:",
						exception);
				throw exception;
			}
		}

		this.vBox = vbManager.getVBox();
		log.debug(String.format("Using VirtualBox version '%s'",
				vBox.getVersion()));

		// no delay, continue with network usage immediately
		this.setLinkUpDelay(0);
	}

	public boolean containsMachine(final String vmName) {
		if (vmName == null || vmName.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"vmName must not be null or empty");
		}

		List<IMachine> machines = vBox.getMachinesByGroups(getBetsyGroups());

		if (machines.isEmpty()) {
			log.info("VirtualBox does not contain any machines yet.");
			return false;
		}

		for (IMachine vm : machines) {
			log.trace(String.format("Found machine '%s'", vm.getName()));
			if (vm.getName().equals(vmName)) {
				return true;
			}

		}
		return false;
	}

	public void importEngine(final String vmName, final String engineName,
			final File importFile) {
		if (vmName == null || vmName.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"The name of the vm to import must not be null or empty");
		}
		if (engineName == null || engineName.trim().isEmpty()) {
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
			IAppliance appliance = vBox.createAppliance();

			IProgress readProgress = appliance.read(importFile
					.getAbsolutePath());
			while (!readProgress.getCompleted()) {
				// loop with percentage feedback every 1s
				readProgress.waitForCompletion(1000);
			}

			appliance.interpret();
			List<String> warnings = appliance.getWarnings();

			for (String warning : warnings) {
				log.warn("Import warning: " + warning);
			}

			IProgress importProgress = appliance.importMachines(null);
			while (!importProgress.getCompleted()) {
				// loop with percentage feedback every 1s
				importProgress.waitForCompletion(1000);
			}
			log.trace("Appliance import done!");

			for (String uuid : appliance.getMachines()) {
				importedVm = vBox.findMachine(uuid);

				// acquire session lock
				session = vbManager.getSessionObject();
				importedVm.lockMachine(session, LockType.Write);
				IMachine lockedVM = session.getMachine();

				lockedVM.setName(vmName);
				String desc = lockedVM.getDescription();
				if (!desc.isEmpty()) {
					desc += "\n\n";
				}
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEE, d MMM yyyy HH:mm");
				desc += "Imported via VBoxWebSrv with betsy on "
						+ sdf.format(date);
				lockedVM.setDescription(desc);
				lockedVM.setGroups(getBetsyGroups());

				// disable the audio adapter, preventing driver issues
				lockedVM.getAudioAdapter().setEnabled(false);

				// remove shared folders
				List<ISharedFolder> sharedFolders = lockedVM.getSharedFolders();
				for (ISharedFolder folder : sharedFolders) {
					lockedVM.removeSharedFolder(folder.getName());
				}

				// save all settings and make them persistent
				lockedVM.saveSettings();

				ISystemProperties sprops = vBox.getSystemProperties();
				ChipsetType chipset = lockedVM.getChipsetType();
				Long maxNetworkAdapters = sprops.getMaxNetworkAdapters(chipset);

				// boolean foundNAT = false;
				String macRegex = "^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$";
				Pattern macPattern = Pattern.compile(macRegex,
						Pattern.CASE_INSENSITIVE);
				String macAddress = config
						.getValueAsString("virtualisation.engines."
								+ engineName + ".mac");
				boolean validMACAddress = false;
				Matcher matcher = macPattern.matcher(macAddress);
				if (matcher.matches()) {
					log.debug("Found valid MAC address to set in config");
					validMACAddress = true;
					macAddress = macAddress.replaceAll("-", "");
					macAddress = macAddress.replaceAll(":", "");
				} else {
					log.info("No MAC address to apply found");
				}

				// TODO should be unimportant if adapter is of type NAT or not
				// important would be to know the MAC address

				// adapt network and set NAT only
				// for (Long slot = 0l; slot < maxNetworkAdapters; slot++) {
				// INetworkAdapter na = lockedVM.getNetworkAdapter(slot);
				//
				// if (na.getAttachmentType()
				// .equals(NetworkAttachmentType.NAT)
				// && na.getEnabled()) {
				// foundNAT = true;
				// if (validMACAddress) {
				// na.setMACAddress(macAddress);
				// }
				// // using one adapter is enough
				// break;
				// }
				// }

				// if there is no NAT adapter, then make first adapter to it
				// BUT: only if there is a MAC address to set, preventing
				// network failure
				if (validMACAddress) {
					for (Long slot = 0l; slot < maxNetworkAdapters; slot++) {
						INetworkAdapter na = lockedVM.getNetworkAdapter(slot);
						if (slot == 0) {
							// then set first adapter to NAT
							na.setAdapterType(NetworkAdapterType.I82540EM);
							na.setEnabled(true);
							na.setAttachmentType(NetworkAttachmentType.NAT);
							na.setMACAddress(macAddress);
						} else {
							// disable every other adapter
							na.setEnabled(false);
						}
					}
				} else {
					// no MAC available, set to NAT only
					INetworkAdapter na = lockedVM.getNetworkAdapter(0l);
					na.setAdapterType(NetworkAdapterType.I82540EM);
					na.setEnabled(true);
					na.setAttachmentType(NetworkAttachmentType.NAT);
				}

				// save all network settings and make them persistent
				lockedVM.saveSettings();

				try {
					session.unlockMachine();
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

	public IMachine getMachine(final String name)
			throws VirtualMachineNotFoundException {
		List<IMachine> machines = vBox.getMachinesByGroups(getBetsyGroups());
		for (IMachine machine : machines) {
			if (machine.getName().equals(name)) {
				return machine;
			}
		}

		throw new VirtualMachineNotFoundException("VirtualMachine with name '"
				+ name + "' could not be found in betsy's VirtualBox group.");
	}

	public File getVBoxVirtualMachineFolder() {
		// get default vm folder
		File defMachineFolder = new File(vBox.getSystemProperties()
				.getDefaultMachineFolder());
		if (defMachineFolder.isDirectory()) {
			return defMachineFolder;
		}
		// backup solution, should not happen
		return new File("virtualmachines");
	}

	public boolean isHardwareVirtualisationSupported() {
		IHost host = vBox.getHost();
		boolean vtx = host.getProcessorFeature(ProcessorFeature.HWVirtEx);
		log.trace("Host Supports VT-X? " + vtx);
		return vtx;
	}

	// TODO purpose?
	public void deleteMachine(final IMachine machine) {
		File logFolder = new File(machine.getLogFolder());
		logFolder.delete();

		List<IMedium> removableMediums = machine.unregister(CleanupMode.Full);
		machine.delete(removableMediums);
	}

	public void deleteMachine(final String vmName)
			throws VirtualMachineException {
		IMachine machine = getMachine(vmName);
		deleteMachine(machine);
	}

	private void setLinkUpDelay(int milliSeconds) {
		/*
		 * The LinkUpDelay specifies how many milliseconds the network adapter
		 * of the guest machine remains silent until he resumes his work stored
		 * in his networkstack
		 */
		if (!vBox.getExtraData(
				"VBoxInternal/Devices/e1000/0/Config/LinkUpDelay").equals(
				Integer.toString(milliSeconds))) {

			log.info("Disabling LinkUpDelay for this VirtualBox instance...");
			vBox.setExtraData(
					"VBoxInternal/Devices/e1000/0/Config/LinkUpDelay",
					Integer.toString(milliSeconds));
		}
	}

	private List<String> getBetsyGroups() {
		List<String> groups = new LinkedList<>();
		groups.add(BETSY_VBOX_GROUP);
		return groups;
	}

}
