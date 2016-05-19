package betsy.bpel.virtual.host.engines;

import betsy.bpel.engines.petalsesb.PetalsEsbEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.common.messages.deploy.FileMessage;
import betsy.bpel.virtual.host.ServiceAddress;
import betsy.common.timeouts.timeout.TimeoutRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static betsy.common.config.Configuration.get;

public class VirtualPetalsEsbEngine extends AbstractVirtualBPELEngine {

    public static final int HTTP_PORT = 8084;

    public VirtualPetalsEsbEngine() {
        super();
        this.defaultEngine = new PetalsEsbEngine();
    }

    @Override
    public List<ServiceAddress> getVerifiableServiceAddresses() {
        List<ServiceAddress> saList = new LinkedList<>();
        saList.add(new ServiceAddress("http://localhost:" + HTTP_PORT + "/petals/services/listServices"));
        return saList;
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(HTTP_PORT);
        return portList;
    }

    @Override
    public String getEndpointUrl(BPELProcess process) {
        return defaultEngine.getEndpointUrl(process);
    }

    @Override
    public void buildArchives(BPELProcess process) {
        defaultEngine.buildArchives(process);
    }

    @Override
    public Path getXsltPath() {
        return defaultEngine.getXsltPath();
    }

    @Override
    public DeployRequest buildDeployRequest(BPELProcess process) throws IOException {
        DeployRequest operation = new DeployRequest();
        operation.setFileMessage(FileMessage.build(process.getTargetPackageCompositeFilePath()));
        operation.setEngineName(getName());
        operation.setProcessName(process.getName());
        operation.setDeploymentLogFilePath(get("virtual.engines.petalsesb_v.deploymentLogFile"));
        operation.setDeploymentDir(get("virtual.engines.petalsesb_v.deploymentDir"));
        operation.setDeployTimeout(TimeoutRepository.getTimeout("petalsesb_v.deployment"));

        return operation;
    }

    @Override
    public LogFilesRequest buildLogFilesRequest() {
        LogFilesRequest request = new LogFilesRequest();
        request.getPaths().add(get("virtual.engines.petalsesb_v.bvmsDir") + "/log");
        request.getPaths().add(get("virtual.engines.petalsesb_v.logfileDir"));
        return request;
    }

    @Override
    public boolean getHeadlessModeOption() {
        return Boolean.valueOf(get("virtual.engines.petalsesb_v.headless"));
    }

    @Override
    public boolean saveStateInsteadOfShutdown() {
        return Boolean.valueOf(get("virtual.engines.petalsesb_v.shutdownSaveState"));
    }

}
