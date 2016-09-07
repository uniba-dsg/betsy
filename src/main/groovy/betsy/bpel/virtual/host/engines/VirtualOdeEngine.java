package betsy.bpel.virtual.host.engines;

import betsy.bpel.engines.ode.OdeEngine;
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

public class VirtualOdeEngine extends AbstractVirtualBPELEngine {
    public static final int HTTP_PORT = 8080;

    public VirtualOdeEngine() {
        super();
        this.defaultEngine = new OdeEngine();
    }

    @Override
    public List<ServiceAddress> getVerifiableServiceAddresses() {
        List<ServiceAddress> saList = new LinkedList<>();
        saList.add(new ServiceAddress("http://localhost:" + HTTP_PORT + "/ode"));
        return saList;
    }

    @Override
    public String getEndpointUrl(String name) {
        // is not delegated because of the dependency to the local Tomcat
        return "http://localhost:" + HTTP_PORT + "/ode/processes/" + name + "TestInterface";
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(HTTP_PORT);
        return portList;
    }

    @Override
    public DeployRequest buildDeployRequest(String name, Path path) throws IOException {
        DeployRequest operation = new DeployRequest();
        operation.setFileMessage(FileMessage.build(path));
        operation.setEngineName(getName());
        operation.setProcessName(name);
        operation.setDeploymentLogFilePath(get("virtual.engines.ode_v.deploymentLogFile"));
        operation.setDeploymentDir(get("virtual.engines.ode_v.deploymentDir"));
        operation.setDeployTimeout(TimeoutRepository.getTimeout("ode_v.deployment"));

        return operation;
    }

    @Override
    public LogFilesRequest buildLogFilesRequest() {
        LogFilesRequest request = new LogFilesRequest();
        request.getPaths().add(get("virtual.engines.ode_v.bvmsDir") + "/log");
        request.getPaths().add(get("virtual.engines.ode_v.logfileDir"));
        return request;
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
    public boolean getHeadlessModeOption() {
        return Boolean.valueOf(get("virtual.engines.ode_v.headless"));
    }

    @Override
    public boolean saveStateInsteadOfShutdown() {
        return Boolean.valueOf(get("virtual.engines.ode_v.shutdownSaveState"));
    }

}
