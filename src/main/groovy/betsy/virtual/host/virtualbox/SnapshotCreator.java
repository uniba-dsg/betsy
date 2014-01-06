package betsy.virtual.host.virtualbox;

import betsy.config.Configuration;
import betsy.virtual.host.ServiceAddress;
import betsy.virtual.host.VirtualBoxException;
import betsy.virtual.host.VirtualBoxMachine;
import betsy.virtual.host.engines.EngineNamingConstants;
import betsy.virtual.host.exceptions.VirtualEngineServiceException;
import betsy.virtual.host.virtualbox.utils.ServiceValidator;
import betsy.virtual.host.virtualbox.utils.Timeouts;
import betsy.virtual.host.virtualbox.utils.port.PortUsageException;
import betsy.virtual.host.virtualbox.utils.port.PortVerifier;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class SnapshotCreator {

    private static final Logger log = Logger.getLogger(SnapshotCreator.class);

    private final VirtualBoxMachine virtualBoxMachine;
    private final String engineName;
    private final List<ServiceAddress> engineServices;
    private final Set<Integer> forwardingPorts;

    public SnapshotCreator(VirtualBoxMachine virtualBoxMachine, String engineName, List<ServiceAddress> engineServices, Set<Integer> forwardingPorts) {
        this.virtualBoxMachine = virtualBoxMachine;
        this.engineName = engineName;
        this.engineServices = engineServices;
        this.forwardingPorts = forwardingPorts;
    }

    public void invoke() throws VirtualBoxException {
        if (virtualBoxMachine.hasRunningSnapshot()) {
            return;
        }

        if (StringUtils.isBlank(engineName)) {
            throw new IllegalArgumentException("The name of the engine to "
                    + "import must not be blank");
        }
        if (engineServices == null || engineServices.isEmpty()) {
            throw new IllegalArgumentException("The list of services to verify"
                    + " if a vm has been started must not be null or empty");
        }

        if (virtualBoxMachine.isActive()) {
            throw new IllegalStateException("Can't create a running snapshot "
                    + "if the VM is already active. The VM should be "
                    + "poweredOff!");
        }

        try {
            PortVerifier.failForUsedPorts(forwardingPorts);
        } catch (PortUsageException e) {
            throw new VirtualBoxException("ports could not be forwarded", e);
        }

        log.debug("Create running-state snapshot");

        try {
            // configure
            virtualBoxMachine.applyPortForwarding(forwardingPorts);

            virtualBoxMachine.start();

            // ensure all is up and running
            failIfEngineServicesTimeout(engineName, engineServices);
            failIfBetsyServerTimesout();

            virtualBoxMachine.takeSnapshot(createSnapshotName(engineName), createSnapshotDescription());

            virtualBoxMachine.stop();
        } finally {
            // stop if vm is still running
            if (virtualBoxMachine.isRunning()) {
                log.warn("VM was still running, stop now");
                virtualBoxMachine.stop();
            }
        }
    }

    private void failIfEngineServicesTimeout(String engineName, List<ServiceAddress> engineServices) throws VirtualEngineServiceException {
        final int secondsToWait = Configuration.getValueAsInteger("virtual.engines." + engineName + ".serviceTimeout");

        try {
            if (!ServiceValidator.isEngineReady(engineServices, secondsToWait)) {
                log.warn("engine services not found withing " + secondsToWait + " seconds");
                // timeout in CountDownLatch
                throw new VirtualEngineServiceException(
                        "The required services for the engine were "
                                + "not available within " + secondsToWait
                                + "s after starting the vm. If using a debian/"
                                + "ubuntu system, make sure to delete the "
                                + "'/etc/udev/rules.d/70-persistent-net.rules'"
                                + "file before exporting the appliance. "
                                + "If this error occurs "
                                + "repeatedly, please import the vm manually"
                                + " with a valid snapshot in 'Running' state.");
            }
        } catch (MalformedURLException e) {
            throw new VirtualEngineServiceException("Could not verify "
                    + "engine service availability. At least one address is "
                    + "invalid: ", e);
        } catch (InterruptedException e) {
            throw new VirtualEngineServiceException("interrupted", e);
        }
    }

    private void failIfBetsyServerTimesout() throws VirtualEngineServiceException {
        if (!ServiceValidator.isBetsyServerReady(Timeouts.FIFTEEN_SECONDS)) {
            log.warn("betsy server not found withing 15s");
            throw new VirtualEngineServiceException(
                    "The required betsy server was "
                            + "not available within 15s "
                            + "after having found all other services. ");
        }
    }

    private String createSnapshotName(String engineName) {
        return EngineNamingConstants.VIRTUAL_NAME_PREFIX + engineName + "_import-snapshot";
    }

    private String createSnapshotDescription() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        return "Machine is in 'saved' state. Snapshot created during import on " + sdf.format(date);
    }


}
