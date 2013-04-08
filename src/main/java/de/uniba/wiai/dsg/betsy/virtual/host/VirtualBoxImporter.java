package de.uniba.wiai.dsg.betsy.virtual.host;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.virtualbox_4_2.ChipsetType;
import org.virtualbox_4_2.IAppliance;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.ISharedFolder;
import org.virtualbox_4_2.ISystemProperties;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.NetworkAdapterType;
import org.virtualbox_4_2.NetworkAttachmentType;

import de.uniba.wiai.dsg.betsy.Configuration;

/**
 * Offers methods to import an Appliance and adjust the settings of an imported
 * {@link IMachine} to be compatible with betsy.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualBoxImporter {

	private final Configuration config = Configuration.getInstance();
	private final Logger log = Logger.getLogger(getClass());

	private final IVirtualBox vBox;

	public VirtualBoxImporter(final IVirtualBox vBox) {
		this.vBox = vBox;
	}

	/**
	 * Import the appliance from the given {@link File}.
	 * 
	 * @param importFile
	 *            file of the appliance to import
	 * @return imported {@link IAppliance}
	 */
	public IAppliance importAppliance(final File importFile) {
		IAppliance appliance = vBox.createAppliance();

		IProgress readProgress = appliance.read(importFile.getAbsolutePath());
		while (!readProgress.getCompleted()) {
			readProgress.waitForCompletion(1000);
		}

		appliance.interpret();
		List<String> warnings = appliance.getWarnings();

		for (String warning : warnings) {
			log.warn("Import warning: " + warning);
		}

		// no special import options needed
		IProgress importProgress = appliance.importMachines(null);
		while (!importProgress.getCompleted()) {
			importProgress.waitForCompletion(1000);
		}
		log.trace("Appliance import done!");
		return appliance;
	}

	/**
	 * Adjust the settings of the given machine.<br>
	 * <br>
	 * This includes:
	 * <ul>
	 * <li>Setting name</li>
	 * <li>Setting description</li>
	 * <li>Setting group</li>
	 * <li>Disable audio adapter</li>
	 * <li>Remove shared folders</li>
	 * <li>Setting NAT Adapter and it's MAC Address</li>
	 * </ul>
	 * 
	 * @param lockedVM
	 *            mutable {@link IMachine} to adjust
	 * @param vmName
	 *            desired name of the machine
	 * @param engineName
	 *            name of the engine the vm belongs to
	 */
	public void adjustMachineSettings(final IMachine lockedVM,
			final String vmName, final String engineName) {
		// set name and description
		lockedVM.setName(vmName);
		String desc = lockedVM.getDescription();
		if (!desc.isEmpty()) {
			desc += "\n\n";
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
		desc += "Imported via VBoxWebSrv with betsy on " + sdf.format(date);
		lockedVM.setDescription(desc);

		// set group
		List<String> groups = new LinkedList<>();
		groups.add(VirtualBoxController.BETSY_VBOX_GROUP);
		lockedVM.setGroups(groups);

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
		Pattern macPattern = Pattern
				.compile(macRegex, Pattern.CASE_INSENSITIVE);
		String macAddress = config.getValueAsString("virtualisation.engines."
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
	}

}
