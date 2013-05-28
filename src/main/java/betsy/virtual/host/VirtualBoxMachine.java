package betsy.virtual.host;

import java.util.List;
import java.util.Set;

public interface VirtualBoxMachine {

    // TODO set headless per setter?

    void start(final boolean headless);
    void stop();
    void kill();

    void saveState();

    boolean isRunning();
    boolean isActive();

    void resetToLatestSnapshot();
    void createRunningSnapshot(final String engineName,
                               final List<ServiceAddress> engineServices,
                               final Set<Integer> forwardingPorts, final boolean headless)
            throws VirtualBoxException, InterruptedException;

    void clearPortForwarding() throws VirtualBoxException;
    void applyPortForwarding(final Set<Integer> forwardingPorts) throws VirtualBoxException;

}
