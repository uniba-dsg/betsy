package betsy.virtual.host.virtualbox;

import betsy.tasks.FileTasks;
import betsy.virtual.host.VirtualBoxMachine;
import betsy.virtual.host.exceptions.vm.VirtualMachineNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.virtualbox_4_2.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    private final Map<String, VirtualBoxMachineImpl> virtualMachines = new HashMap<>();

    private IVirtualBox vBox;
    private VBoxApplianceImporter vBoxImporter;
    private VirtualBoxManager vBoxManager;

    public VBoxController() {
        log.trace("Initializing VBoxController");
        VBoxConnector vBoxConn = new VBoxConnector();
        this.vBox = vBoxConn.connect();
        this.vBoxManager = vBoxConn.getVBoxManager();
        this.vBoxImporter = vBoxConn.getVBoxImporter();

        log.trace("VirtualBoxController initialized");
    }

    /**
     * Import the Engine's virtualMachine from the given file.
     *
     * @param vmName     desired name of the virtualMachine
     * @param engineName name of the engine the new VM belongs to
     * @param importFile file of the appliance to import
     */
    public void importVirtualMachine(final String vmName, final String engineName, final Path importFile) {

        if (StringUtils.isBlank(vmName)) {
            throw new IllegalArgumentException(
                    "The name of the vm to import must not be null or empty");
        }
        if (StringUtils.isBlank(engineName)) {
            throw new IllegalArgumentException(
                    "The name of the engine to import must not be null or empty");
        }
        Objects.requireNonNull(importFile, "The file to import must not be null");

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
                } catch (VBoxException ignore) {
                    log.debug("Failed to unlock session after import exception", ignore);
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
     * Get the {@link VirtualBoxMachineImpl} of betsy with the given name.
     *
     * @param name name of the VirtualMachine to get
     * @return VirtualBoxMachine with the searched name
     * @throws VirtualMachineNotFoundException thrown if there is no VirtualMachine with this name
     */
    public VirtualBoxMachine getVirtualMachine(final String name)
            throws VirtualMachineNotFoundException {
        // TODO warum diese zwischenspeicherung der VMs? Ist dies aus Performanzgründen notwendig?
        if (virtualMachines.containsKey(name)) {
            return virtualMachines.get(name);
        } else {
            VirtualBoxMachineImpl vm = new VirtualBoxMachineImpl(vBoxManager,
                    getMachine(name));
            virtualMachines.put(name, vm);
            return vm;
        }
    }

    /**
     * Get the {@link IMachine} of VirtualBox with the given name.
     *
     * @param name name of the IMachine to get
     * @return IMachine with the searched name
     * @throws VirtualMachineNotFoundException thrown if there is no IMachine with this name
     */
    public IMachine getMachine(final String name)
            throws VirtualMachineNotFoundException {

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException(
                    "name of the virtual machine must not be null or empty");
        }

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

    private void deleteMachine(final IMachine machine) {
        Path logFolder = Paths.get(machine.getLogFolder());
        FileTasks.deleteDirectory(logFolder);

        List<IMedium> removableMediums = machine.unregister(CleanupMode.Full);
        machine.delete(removableMediums);
    }

}
