package betsy.virtual.host.engines;

import betsy.Configuration;
import betsy.data.BetsyProcess;
import betsy.data.engines.Engine;
import betsy.virtual.common.Constants;
import betsy.virtual.common.messages.collect_log_files.LogFile;
import betsy.virtual.common.messages.collect_log_files.LogFiles;
import betsy.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.virtual.common.messages.deploy.DeployRequest;
import betsy.virtual.host.VirtualBox;
import betsy.virtual.host.VirtualBoxException;
import betsy.virtual.host.VirtualBoxMachine;
import betsy.virtual.host.VirtualizedEngineAPI;
import betsy.virtual.host.comm.HostTcpClient;
import betsy.virtual.host.exceptions.DownloadException;
import betsy.virtual.host.exceptions.PermanentFailedTestException;
import betsy.virtual.host.exceptions.TemporaryFailedTestException;
import betsy.virtual.host.exceptions.VirtualizedEngineServiceException;
import betsy.virtual.host.exceptions.archive.ArchiveException;
import betsy.virtual.host.exceptions.vm.PortRedirectException;
import betsy.virtual.host.exceptions.vm.VirtualMachineNotFoundException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * A {@link VirtualEngine} does not install and use an engine server on the
 * current host, but does use an engine installed in a virtualized environment.
 * It therefore offers the same functionality as the default {@link Engine} but
 * also has some more functions to handle the virtualized surrounding.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public abstract class VirtualEngine extends Engine implements
        VirtualizedEngineAPI {

    private static final Logger log = Logger.getLogger(VirtualEngine.class);
    public static final int SECOND = 1000;

    private VirtualBox virtualBox;
    private final HostTcpClient comm = new HostTcpClient(Constants.SERVER_HOSTNAME, Constants.SERVER_PORT);

    public Engine defaultEngine;

    private VirtualBoxMachine vm = null;

    private static File getDownloadPath() {
        return new File("vm_download");
    }

    private static File getExtractPath() {
        return new File("vm_extraction");
    }

    public void setVirtualBox(VirtualBox virtualBox) {
        this.virtualBox = virtualBox;
    }

    @Override
    public String getVirtualMachineName() {
        return EngineNamingConstants.VIRTUAL_NAME_PREFIX + this.getName();
    }

    @Override
    public void startup() {
        if (useRunningVM()) {
            return;
        }

        log.debug("Startup virtualized engine " + getName() + " ...");
        // required for compatibility with EngineControl
        try {
            // verify port usage
            Set<Integer> ports = getRequiredPorts();
            // also verify the bVMS port
            ports.add(Constants.SERVER_PORT);

            // forward and verify used ports
            this.vm.applyPortForwarding(ports);
            // start headless?
            boolean headless = Configuration.getValueAsBoolean(
                    "virtual.engines." + getName() + ".headless");
            this.vm.start(headless);
            log.debug("...VM started");
        } catch (PortRedirectException exception) {
            throw new TemporaryFailedTestException("The VM could not be "
                    + "started properly:", exception);
        } catch (VirtualBoxException e) {
            throw new PermanentFailedTestException(e);
        }
        log.trace("...startup done!");
    }

    @Override
    public void shutdown() {
        if (useRunningVM()) {
            return;
        }

        log.debug("Shutdown virtualized engine " + getName() + " ...");
        // stop communication
        // if there is no virtualMachine then there is nothing to stop
        if (this.vm != null) {
            boolean saveState = Configuration.getValueAsBoolean(
                    "virtual.engines." + getName()
                            + ".shutdownSaveState");
            if (saveState) {
                this.vm.saveState();
            } else {
                this.vm.stop();
            }
        }
        log.trace("...shutdown done!");
    }

    @Override
    public void install() {
        if (useRunningVM()) {
            return;
        }

        log.debug("Install virtualized engine " + getName() + " ...");

        try {
            this.vm = getOrImportVirtualMachine();
            createAndResetToLatestSnapshot();
        } catch (VirtualMachineNotFoundException
                | VirtualizedEngineServiceException | ArchiveException
                | DownloadException exception) {
            throw new PermanentFailedTestException("The VMs installation "
                    + "could not be processed:", exception);
        } catch (PortRedirectException | InterruptedException exception) {
            throw new TemporaryFailedTestException("The VMs installation "
                    + "could not be processed:", exception);
        } catch (VirtualBoxException e) {
            throw new PermanentFailedTestException(e);
        }
        log.trace("...installation done!");
    }

    private void createAndResetToLatestSnapshot() throws VirtualBoxException, InterruptedException {
        boolean headless = Configuration
                .getValueAsBoolean("virtual.engines."
                        + getName() + ".headless");
        // need to create a running snapshot
        this.vm.createRunningSnapshot(getName(),
                getVerifiableServiceAddresses(),
                getRequiredPorts(), headless);

        // reset to snapshot
        this.vm.resetToLatestSnapshot();
    }

    private VirtualBoxMachine getOrImportVirtualMachine() throws VirtualBoxException {
        return virtualBox.importVirtualMachine(getVirtualMachineName(), getName(),
                getDownloadPath(), getExtractPath());
    }

    @Override
    public void deploy(BetsyProcess process) {
        try {
            log.info("Deploying virtualized engine " + getName() + ", process: " + process.toString() + " ...");

            // TODO evtl. wiederholen des deployments

            DeployRequest container = buildDeployRequest(process);
            comm.deployOperation(container);
            log.info("...deploy done!");
        } catch (Exception exception) {
            log.info("error during deployment - collecting logs", exception);
            try {
                storeLogs(process);
                throw new RuntimeException(exception);
            } catch (Exception exception2) {
                log.info("Could not store logfiles of the failed deployment:", exception2);
                throw new RuntimeException(exception);
            }
        }
    }

    @Override
    public void storeLogs(BetsyProcess process) {
        log.debug("Storing logs for engine " + getName() + " ...");

        LogFilesRequest request = buildLogFilesRequest(process);

        // TODO evtl. mittels RETRY wiederholen lassen.

        try {
            LogFilesResponse response = comm.collectLogFilesOperation(request);

            // create log folders
            File processLogFolder = new File(process.getTargetPath() + "/logs");
            processLogFolder.mkdirs();

            // save to disk...
            for (LogFiles logFiles : response.getLogFiles()) {
                File folder = new File(processLogFolder, logFiles.getFolder().replaceAll("/", "_"));
                folder.mkdirs();
                for (LogFile logFile : logFiles.getLogFiles()) {
                    Path path = new File(folder, logFile.getFilename()).toPath();
                    try {
                        Files.write(path, logFile.getContent(), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new IllegalStateException("could not write file", e);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void failIfRunning() {
        if (useRunningVM()) {
            return;
        }

        // raise exception if VM is imported AND is already running
        if (vm == null) {
            try {
                vm = getVirtualMachine();
            } catch (VirtualBoxException e) {
                // ignore, can't be running if not found
                return;
            }
        }

        if (vm.isActive()) {
            throw new PermanentFailedTestException("VirtualMachine is already running.");
        }
    }

    private boolean useRunningVM() {
        return Configuration.getValueAsBoolean("virtual.useRunningVM");
    }

    private VirtualBoxMachine getVirtualMachine() throws VirtualBoxException {
        return virtualBox.getVirtualMachineByName(getVirtualMachineName());
    }

}
