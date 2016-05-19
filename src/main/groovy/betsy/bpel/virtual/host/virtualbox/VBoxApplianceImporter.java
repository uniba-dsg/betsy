package betsy.bpel.virtual.host.virtualbox;

import org.apache.log4j.Logger;
import org.virtualbox_4_2.*;
import betsy.common.timeouts.timeout.TimeoutRepository;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Offers methods to import an Appliance and adjust the settings of an imported
 * {@link IMachine} to be compatible with betsy.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
class VBoxApplianceImporter {

	private static final Logger log = Logger.getLogger(VBoxApplianceImporter.class);
	
	private final IVirtualBox vBox;

	public VBoxApplianceImporter(final IVirtualBox vBox) {
		this.vBox = vBox;
	}

	/**
	 * Import the appliance from the given {@link File}.
	 * 
	 * @param importFile
	 *            file of the appliance to import
	 * @return imported {@link IAppliance}
	 */
	public IAppliance importAppliance(final Path importFile) {
		IAppliance appliance = vBox.createAppliance();

		IProgress readProgress = appliance.read(importFile.toAbsolutePath().toString());
		while (!readProgress.getCompleted()) {
			readProgress.waitForCompletion(TimeoutRepository.getTimeout("VBoxApplianceImporter.importAppliance.readProgress").getTimeoutInMs());
		}

		appliance.interpret();

        logWarnings(appliance);

		// keep NAT MAC addresses
		List<ImportOptions> options = new LinkedList<>();
		options.add(ImportOptions.KeepNATMACs);
		IProgress importProgress = appliance.importMachines(options);
		while (!importProgress.getCompleted()) {
			importProgress.waitForCompletion(TimeoutRepository.getTimeout("VBoxApplianceImporter.importAppliance.importProgress").getTimeoutInMs());
		}

        logWarnings(appliance);

		log.trace("Appliance import done!");
		return appliance;
	}

    private void logWarnings(IAppliance appliance) {
        List<String> warnings = appliance.getWarnings();
        for (String warning : warnings) {
            log.warn("Import warning: " + warning);
        }
    }

    /**
	 * Adjust the settings of the given appliance.<br>
	 * <br>
	 * This includes:
	 * <ul>
	 * <li>Setting name</li>
	 * <li>Setting description</li>
	 * <li>Setting group</li>
	 * <li>Disable audio adapter</li>
	 * <li>Remove shared folders</li>
	 * </ul>
	 * 
	 * @param lockedVM
	 *            mutable {@link IMachine} to adjust
	 * @param vmName
	 *            desired name of the machine
	 */
	public void adjustApplianceSettings(final IMachine lockedVM,
			final String vmName) {
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
		groups.add(VBoxController.BETSY_VBOX_GROUP);
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
	}

}
