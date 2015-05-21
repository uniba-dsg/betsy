package betsy.bpel.virtual.host.virtualbox;

import betsy.common.config.Configuration;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import org.apache.log4j.Logger;

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The {@link VirtualMachineImporter} offers several methods to prepare and
 * finally execute the import of a VirtualBox appliance.<br>
 * <br>
 * The default import procedure involves:
 * <ul>
 * <li>Download the VM</li>
 * <li>Extract the archive</li>
 * <li>Import the Appliance</li>
 * <li>Cleanup extraction path</li>
 * </ul>
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualMachineImporter {

    private static final Logger log = Logger.getLogger(VirtualMachineImporter.class);

    private final VBoxController vbc;
    private final String vmName;
    private final String engineName;
    private final Path downloadPath;

    public VirtualMachineImporter(final String vmName, final String engineName, final Path downloadPath, final VBoxController vbc) {
        this.vbc = Objects.requireNonNull(vbc);
        this.downloadPath = Objects.requireNonNull(downloadPath);

        this.vmName = Objects.requireNonNull(vmName);
        this.engineName = Objects.requireNonNull(engineName);
    }

    public void makeVMAvailable() {
        log.info("Downloading VM " + vmName + " to " + downloadPath);
        FileTasks.mkdirs(downloadPath);
        NetworkTasks.downloadFile(getDownloadUrl(), downloadPath);

        log.info("Importing VM " + vmName + " into VirtualBox");
        vbc.importVirtualMachine(vmName, engineName, getDownloadArchiveFile());

        log.info("Import finished");
    }

    private String getDownloadUrl() {
        return Configuration.get("virtual.engines." + engineName + ".download");
    }

    private Path getDownloadArchiveFile() {
        String url = getDownloadUrl();
        // get only the fileName + extension without the directory structure
        String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
        return downloadPath.resolve(fileName);
    }


}
