package betsy.virtual.host.virtualbox;

import betsy.virtual.host.VirtualBox;
import betsy.virtual.host.VirtualBoxMachine;
import betsy.virtual.host.exceptions.DownloadException;
import betsy.virtual.host.exceptions.archive.ArchiveException;
import betsy.virtual.host.exceptions.vm.VirtualMachineNotFoundException;

import java.io.File;

public class VirtualBoxImpl implements VirtualBox {

    private VBoxController controller = new VBoxController();

    @Override
    public VirtualBoxMachine getVirtualMachineByName(String name) throws VirtualMachineNotFoundException {
        return controller.getVirtualMachine(name);
    }

    @Override
    public VirtualBoxMachine importVirtualMachine(String vmName, String engineName, File downloadPath, File extractPath)
            throws VirtualMachineNotFoundException, ArchiveException, DownloadException {

        try {
            return getVirtualMachineByName(vmName);
        } catch (Exception ignore) {
            // not imported - proceed
        }

        VirtualMachineImporter importer = new VirtualMachineImporter(
                vmName, engineName,
                downloadPath, extractPath,
                controller);

        importer.makeVMAvailable();

        return getVirtualMachineByName(vmName);
    }
}
