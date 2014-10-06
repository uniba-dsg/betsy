package betsy.bpel.virtual.host;

import java.nio.file.Path;

public interface VirtualBox {

    VirtualBoxMachine getVirtualMachineByName(String name) throws VirtualBoxException;

    VirtualBoxMachine importVirtualMachine(final String vmName, final String engineName,
                                           final Path downloadPath) throws VirtualBoxException;

}
