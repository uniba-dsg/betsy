package betsy.virtual.host;

import java.io.File;

public interface VirtualBox {

    VirtualBoxMachine getVirtualMachineByName(String name) throws VirtualBoxException;

    VirtualBoxMachine importVirtualMachine(final String vmName, final String engineName,
                              final File downloadPath, final File extractPath) throws VirtualBoxException;

}
