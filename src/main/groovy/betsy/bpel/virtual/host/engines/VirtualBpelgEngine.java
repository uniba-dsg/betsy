package betsy.bpel.virtual.host.engines;

import betsy.common.config.Configuration;
import betsy.bpel.model.BetsyProcess;
import betsy.bpel.engines.bpelg.BpelgEngine;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.common.messages.deploy.FileMessage;
import betsy.bpel.virtual.host.ServiceAddress;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static betsy.common.config.Configuration.get;

public class VirtualBpelgEngine extends VirtualEngine {

    public static final int HTTP_PORT = 8080;

    public VirtualBpelgEngine() {
        super();
        this.defaultEngine = new BpelgEngine();
    }

    @Override
    public String getName() {
        return "bpelg_v";
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
    public String getEndpointUrl(BetsyProcess process) {
        // is not delegated because of the dependency to the local Tomcat
        return "http://localhost:" + HTTP_PORT + "/bpel-g/services/" + process.getName() + "TestInterfaceService";
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
        operation.setFileMessage(FileMessage.build(process.getTargetPackageFilePath("zip")));
        operation.setEngineName(getName());
        operation.setProcessName(process.getName());
        operation.setDeploymentLogFilePath(get("virtual.engines.bpelg_v.deploymentLogFile"));
        operation.setDeploymentDir(get("virtual.engines.bpelg_v.deploymentDir"));
        operation.setDeployTimeout(Integer.parseInt(get("virtual.engines.bpelg_v.deploymentTimeout")));

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
        return Boolean.valueOf(Configuration.get("virtual.engines.bpelg_v.headless"));
    }

    @Override
    public boolean saveStateInsteadOfShutdown() {
        return Boolean.valueOf(Configuration.get("virtual.engines.bpelg_v.shutdownSaveState"));
    }
}
