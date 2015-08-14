package betsy.bpel.virtual.host.engines;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.virtual.common.Constants;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFile;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFiles;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.host.VirtualBox;
import betsy.bpel.virtual.host.VirtualBoxException;
import betsy.bpel.virtual.host.VirtualBoxMachine;
import betsy.bpel.virtual.host.VirtualEngineAPI;
import betsy.bpel.virtual.host.comm.HostTcpClient;
import betsy.bpel.virtual.host.exceptions.PermanentFailedTestException;
import betsy.bpel.virtual.host.exceptions.TemporaryFailedTestException;
import betsy.bpel.virtual.host.exceptions.VirtualEngineServiceException;
import betsy.bpel.virtual.host.exceptions.vm.PortRedirectException;
import betsy.bpel.virtual.host.exceptions.vm.VirtualMachineNotFoundException;
import betsy.bpel.virtual.host.virtualbox.SnapshotCreator;
import betsy.common.config.Configuration;
import betsy.common.engines.ProcessLanguage;
import betsy.common.model.Engine;
import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.StackTraceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A {@link AbstractVirtualBPELEngine} does not install and use an engine server on the
 * current host, but does use an engine installed in a virtualized environment.
 * It therefore offers the same functionality as the default {@link betsy.bpel.engines.AbstractBPELEngine} but
 * also has some more functions to handle the virtualized surrounding.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public abstract class AbstractVirtualBPELEngine extends AbstractBPELEngine implements VirtualEngineAPI {

    private static final Logger LOGGER = Logger.getLogger(AbstractVirtualBPELEngine.class);

    private VirtualBox virtualBox;
    private final HostTcpClient comm = new HostTcpClient(Constants.SERVER_HOSTNAME, Constants.SERVER_PORT);

    public AbstractBPELEngine defaultEngine;

    @Override
    public Engine getEngineId() {
        Engine engineId = defaultEngine.getEngineId();
        List<String> configuration = new LinkedList<>();
        configuration.addAll(engineId.getConfiguration());
        configuration.add("virtual");
        return new Engine(ProcessLanguage.BPEL, engineId.getName(), engineId.getVersion(), configuration);
    }

    private VirtualBoxMachine vm = null;

    public void setVirtualBox(VirtualBox virtualBox) {
        this.virtualBox = virtualBox;
    }

    @Override
    public String getVirtualMachineName() {
        return EngineNamingConstants.VIRTUAL_NAME_PREFIX + this.getName();
    }

    @Override
    public void startup() {
        if (Configuration.useRunningVM()) {
            return;
        }

        LOGGER.info("Startup virtual engine " + getName());
        // required for compatibility with EngineControl
        try {
            // verify port usage
            Set<Integer> ports = getRequiredPorts();
            // also verify the bVMS port
            ports.add(Constants.SERVER_PORT);

            // forward and verify used ports
            this.vm.applyPortForwarding(ports);
            setHeadlessMode();
            this.vm.start();
            LOGGER.info("VM started");
        } catch (PortRedirectException exception) {
            throw new TemporaryFailedTestException("The VM could not be "
                    + "started properly:", exception);
        } catch (VirtualBoxException e) {
            throw new PermanentFailedTestException(e);
        }
        LOGGER.trace("startup done!");
    }

    private void setHeadlessMode() {
        this.vm.setHeadlessMode(getHeadlessModeOption());
    }

    public abstract boolean getHeadlessModeOption();

    @Override
    public void shutdown() {
        if (Configuration.useRunningVM()) {
            return;
        }

        LOGGER.debug("Shutdown virtual engine " + getName());
        // stop communication
        // if there is no virtualMachine then there is nothing to stop
        if (this.vm != null) {
            if (saveStateInsteadOfShutdown()) {
                this.vm.saveState();
            } else {
                this.vm.stop();
            }
        }
        LOGGER.trace("shutdown done!");
    }

    public abstract boolean saveStateInsteadOfShutdown();

    @Override
    public void install() {
        if (Configuration.useRunningVM()) {
            return;
        }

        LOGGER.debug("Install virtualized engine " + getName());

        try {
            this.vm = getOrImportVirtualMachine();
            createAndResetToLatestSnapshot();
        } catch (VirtualMachineNotFoundException
                | VirtualEngineServiceException e) {
            throw new PermanentFailedTestException("The VMs installation "
                    + "could not be processed:", e);
        } catch (PortRedirectException | InterruptedException exception) {
            throw new TemporaryFailedTestException("The VMs installation "
                    + "could not be processed:", exception);
        } catch (VirtualBoxException e) {
            throw new PermanentFailedTestException(e);
        }
        LOGGER.trace("installation done!");
    }

    @Override
    public void uninstall() {
        throw new UnsupportedOperationException("uninstall is not supported");
    }

    @Override
    public boolean isInstalled() {
        throw new UnsupportedOperationException("uninstall is not supported");
    }

    private void createAndResetToLatestSnapshot() throws VirtualBoxException, InterruptedException {
        this.setHeadlessMode();

        // need to create a running snapshot
        new SnapshotCreator(this.vm, getName(),  getVerifiableServiceAddresses(), getRequiredPorts()).invoke();

        this.vm.restore();
    }

    private VirtualBoxMachine getOrImportVirtualMachine() throws VirtualBoxException {
        return virtualBox.importVirtualMachine(getVirtualMachineName(), getName(),
                Configuration.getVirtualDownloadDir());
    }

    @Override
    public void deploy(BPELProcess process) {
        try {
            LOGGER.info("Deploying virtualized engine " + getName() + ", process: " + process.toString());

            DeployRequest container = buildDeployRequest(process);
            comm.deployOperation(container);
            LOGGER.info("deploy done!");
        } catch (Exception exception) {
            LOGGER.error("error during deployment - collecting logs", StackTraceUtils.deepSanitize(exception));
            try {
                storeLogs(process);
                throw new RuntimeException(exception);
            } catch (Exception exception2) {
                throw new RuntimeException("Could not store logfiles of the failed deployment.", exception2);
            }
        }
    }

    @Override
    public void storeLogs(BPELProcess process) {
        LOGGER.debug("Storing logs for engine " + getName());

        LogFilesRequest request = buildLogFilesRequest();

        try {
            LogFilesResponse response = comm.collectLogFilesOperation(request);

            // create log folders
            Files.createDirectories(process.getTargetLogsPath());

            // save to disk...
            for (LogFiles logFiles : response.getLogFiles()) {

                String normalizedFolderPath = logFiles.getFolder().replaceAll("/", "_");
                Path folder = process.getTargetLogsPath().resolve(normalizedFolderPath);
                Files.createDirectories(folder);

                for (LogFile logFile : logFiles.getLogFiles()) {
                    Path path = folder.resolve(logFile.getFilename());
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
    public boolean isRunning() {
        if (Configuration.useRunningVM()) {
            return false;
        }

        // raise exception if VM is imported AND is already running
        if (vm == null) {
            try {
                vm = getVirtualMachine();
            } catch (VirtualBoxException | NullPointerException ignore) {
                // ignore, can't be running if not found
                return false;
            }
        }

        return vm.isActive();
    }

    private VirtualBoxMachine getVirtualMachine() throws VirtualBoxException {
        return virtualBox.getVirtualMachineByName(getVirtualMachineName());
    }

    @Override
    public List<Path> getLogs() {
        // TODO implement this
        throw new UnsupportedOperationException("not yet implemented");
    }
}
