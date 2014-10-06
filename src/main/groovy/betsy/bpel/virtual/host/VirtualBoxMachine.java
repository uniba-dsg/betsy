package betsy.bpel.virtual.host;

import java.util.Set;

public interface VirtualBoxMachine extends VirtualMachineLifecycle {

    void saveState();

    boolean isRunning();

    void takeSnapshot(final String name, final String desc);
    boolean hasRunningSnapshot();

    void setHeadlessMode(boolean headless);
    void applyPortForwarding(final Set<Integer> forwardingPorts) throws VirtualBoxException;

}
