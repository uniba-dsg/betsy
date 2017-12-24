package betsy.bpel.virtual.host.engines;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.bpel.engines.bpelg.BpelgEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.common.messages.deploy.FileMessage;
import betsy.bpel.virtual.host.ServiceAddress;
import betsy.common.timeouts.timeout.TimeoutRepository;

import static betsy.common.config.Configuration.get;

public class VirtualBpelgEngine extends AbstractVirtualBPELEngine {

    public static final int HTTP_PORT = 8080;

    public VirtualBpelgEngine() {
        super();
        this.defaultEngine = new BpelgEngine();
    }

    @Override
    public List<ServiceAddress> getVerifiableServiceAddresses() {
        List<ServiceAddress> saList = new LinkedList<>();
        saList.add(new ServiceAddress("http://localhost:" + HTTP_PORT + "/bpel-g/services"));
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
        // is not delegated because of the dependency to the local Tomcat
        return "http://localhost:" + HTTP_PORT + "/bpel-g/services/" + name + "TestInterfaceService";
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
        operation.setDeploymentLogFilePath(get("virtual.engines.bpelg_v.deploymentLogFile"));
        operation.setDeploymentDir(get("virtual.engines.bpelg_v.deploymentDir"));
        operation.setDeployTimeout(TimeoutRepository.getTimeout("bpelg_v.deployment"));

        return operation;
    }

    @Override
    public LogFilesRequest buildLogFilesRequest() {
        LogFilesRequest request = new LogFilesRequest();
        request.getPaths().add(get("virtual.engines.bpelg_v.bvmsDir") + "/log");
        request.getPaths().add(get("virtual.engines.bpelg_v.logfileDir"));
        return request;
    }

    @Override
    public boolean getHeadlessModeOption() {
        return Boolean.valueOf(get("virtual.engines.bpelg_v.headless"));
    }

    @Override
    public boolean saveStateInsteadOfShutdown() {
        return Boolean.valueOf(get("virtual.engines.bpelg_v.shutdownSaveState"));
    }
}
