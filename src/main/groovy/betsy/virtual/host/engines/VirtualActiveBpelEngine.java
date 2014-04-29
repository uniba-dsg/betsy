package betsy.virtual.host.engines;

import betsy.config.Configuration;
import betsy.data.BetsyProcess;
import betsy.data.engines.activebpel.ActiveBpelEngine;
import betsy.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.virtual.common.messages.deploy.DeployRequest;
import betsy.virtual.common.messages.deploy.FileMessage;
import betsy.virtual.host.ServiceAddress;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static betsy.config.Configuration.get;

public class VirtualActiveBpelEngine extends VirtualEngine {

    public static final int HTTP_PORT = 8080;

    public VirtualActiveBpelEngine() {
        super();
        this.defaultEngine = new ActiveBpelEngine();
    }

    @Override
    public String getName() {
        return "active_bpel_v";
    }

    @Override
    public List<ServiceAddress> getVerifiableServiceAddresses() {
        List<ServiceAddress> saList = new LinkedList<>();
        saList.add(new ServiceAddress("http://localhost:" + HTTP_PORT + "/active-bpel/services"));
        saList.add(new ServiceAddress("http://localhost:" + HTTP_PORT + "/BpelAdmin/", "Running"));
        return saList;
    }

    @Override
    public String getEndpointUrl(BetsyProcess process) {
        // is not delegated because of the dependency to the local Tomcat
        return "http://localhost:" + HTTP_PORT + "/active-bpel/services/" + process.getName() + "TestInterfaceService";
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(HTTP_PORT);
        return portList;
    }

    @Override
    public void buildArchives(BetsyProcess process) {
        // use default engine's operations
        defaultEngine.buildArchives(process);
    }

    @Override
    public Path getXsltPath() {
        return defaultEngine.getXsltPath();
    }

    @Override
    public DeployRequest buildDeployRequest(BetsyProcess process) throws IOException {
        DeployRequest operation = new DeployRequest();
        operation.setFileMessage(FileMessage.build(process.getTargetPackageFilePath("bpr")));
        operation.setEngineName(getName());
        operation.setProcessName(process.getName());
        operation.setDeploymentLogFilePath(get("virtual.engines.active_bpel_v.deploymentLogFile"));
        operation.setDeploymentDir(get("virtual.engines.active_bpel_v.deploymentDir"));
        operation.setDeployTimeout(Integer.parseInt(get("virtual.engines.active_bpel_v.deploymentTimeout")));

        return operation;
    }

    @Override
    public LogFilesRequest buildLogFilesRequest() {
        LogFilesRequest request = new LogFilesRequest();
        request.getPaths().add(get("virtual.engines.active_bpel_v.bvmsDir") + "/log");
        request.getPaths().add(get("virtual.engines.active_bpel_v.logfileDir"));
        return request;
    }

    @Override
    public boolean getHeadlessModeOption() {
        return Boolean.valueOf(Configuration.get("virtual.engines.active_bpel_v.headless"));
    }

    @Override
    public boolean saveStateInsteadOfShutdown() {
        return Boolean.valueOf(Configuration.get("virtual.engines.active_bpel_v.shutdownSaveState"));
    }
}
