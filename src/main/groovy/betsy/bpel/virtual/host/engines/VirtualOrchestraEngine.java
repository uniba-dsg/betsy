package betsy.bpel.virtual.host.engines;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.bpel.engines.orchestra.OrchestraEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.common.messages.deploy.FileMessage;
import betsy.bpel.virtual.host.ServiceAddress;
import betsy.common.timeouts.timeout.TimeoutRepository;

import static betsy.common.config.Configuration.get;

public class VirtualOrchestraEngine extends AbstractVirtualBPELEngine {

    public static final int HTTP_PORT = 8080;

    public VirtualOrchestraEngine() {
        super();
        this.defaultEngine = new OrchestraEngine();
    }

    @Override
    public List<ServiceAddress> getVerifiableServiceAddresses() {
        List<ServiceAddress> saList = new LinkedList<>();
        saList.add(new ServiceAddress("http://localhost:" + HTTP_PORT + "/orchestra"));
        return saList;
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(HTTP_PORT);
        return portList;
    }

    @Override
    public String getEndpointUrl(String name) {
        return "http://localhost:" + HTTP_PORT + "/orchestra/" + name + "TestInterface";
    }

    @Override
    public Path buildArchives(BPELProcess process) {
        // use default engine's operations
        return defaultEngine.buildArchives(process);
    }

    @Override
    public Path getXsltPath() {
        return defaultEngine.getXsltPath();
    }

    @Override
    public DeployRequest buildDeployRequest(String name, Path path) throws IOException {
        DeployRequest operation = new DeployRequest();
        operation.setFileMessage(FileMessage.build(path));
        operation.setEngineName(getName());
        operation.setProcessName(name);
        operation.setDeploymentLogFilePath(get("virtual.engines.orchestra_v.deploymentLogFile"));
        operation.setDeploymentDir(get("virtual.engines.orchestra_v.deploymentDir"));
        operation.setDeployTimeout(TimeoutRepository.getTimeout("orchestra_v.deployment"));

        return operation;
    }

    @Override
    public LogFilesRequest buildLogFilesRequest() {
        LogFilesRequest request = new LogFilesRequest();
        request.getPaths().add(get("virtual.engines.orchestra_v.bvmsDir") + "/log");
        request.getPaths().add(get("virtual.engines.orchestra_v.logfileDir"));
        return request;
    }

    @Override
    public boolean getHeadlessModeOption() {
        return Boolean.valueOf(get("virtual.engines.orchestra_v.headless"));
    }

    @Override
    public boolean saveStateInsteadOfShutdown() {
        return Boolean.valueOf(get("virtual.engines.orchestra_v.shutdownSaveState"));
    }

}
