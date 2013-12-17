package betsy.virtual.host.virtualbox;

import betsy.virtual.host.VirtualBox;
import betsy.virtual.host.VirtualBoxMachine;
import betsy.virtual.host.exceptions.vm.VirtualMachineNotFoundException;

import java.nio.file.Path;

public class VirtualBoxImpl implements VirtualBox {

    private VBoxController controller = new VBoxController();

    @Override
    public VirtualBoxMachine getVirtualMachineByName(String name) throws VirtualMachineNotFoundException {
        return controller.getVirtualMachine(name);
    }

    @Override
    public VirtualBoxMachine importVirtualMachine(String vmName, String engineName, Path downloadPath)
            throws VirtualMachineNotFoundException {

        try {
            return getVirtualMachineByName(vmName);
        } catch (Exception ignore) {
            // not imported - proceed
        }

        VirtualMachineImporter importer = new VirtualMachineImporter(vmName, engineName, downloadPath, controller);
        importer.makeVMAvailable();

        return getVirtualMachineByName(vmName);
    }

}
