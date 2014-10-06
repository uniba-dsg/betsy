package betsy.bpel.virtual.host.engines;

import betsy.config.Configuration;
import betsy.bpel.model.BetsyProcess;
import betsy.bpel.engines.openesb.OpenEsbEngine;
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

import static betsy.config.Configuration.get;

public class VirtualOpenEsbEngine extends VirtualEngine {

    public static final int BPEL_SERVICES_PORT = 18181;
    public static final int HTTP_PORT = 8383;
    public static final int HTTPS_PORT = 8384;
    public static final int ADMIN_PORT = 4848;

    public VirtualOpenEsbEngine() {
        super();
        this.defaultEngine = new OpenEsbEngine();
    }

    @Override
    public String getName() {
        return "openesb_v";
    }

    @Override
    public List<ServiceAddress> getVerifiableServiceAddresses() {
        List<ServiceAddress> saList = new LinkedList<>();
        saList.add(new ServiceAddress("http://localhost:" + HTTP_PORT + "/"));
        saList.add(new ServiceAddress("http://localhost:" + ADMIN_PORT + "/"));
        return saList;
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(BPEL_SERVICES_PORT);
        portList.add(HTTP_PORT);
        portList.add(HTTPS_PORT);
        portList.add(ADMIN_PORT);
        return portList;
    }

    @Override
    public String getEndpointUrl(BetsyProcess process) {
        return defaultEngine.getEndpointUrl(process);
    }

    @Override
    public void buildArchives(BetsyProcess process) {
        defaultEngine.buildArchives(process);
    }

    @Override
    public Path getXsltPath() {
        return defaultEngine.getXsltPath();
    }

    @Override
    public DeployRequest buildDeployRequest(BetsyProcess process) throws IOException {
        DeployRequest operation = new DeployRequest();

        operation.setFileMessage(FileMessage.build(process.getTargetPackageCompositeFilePath()));
        operation.setEngineName(getName());
        operation.setProcessName(process.getName());
        operation.setDeploymentLogFilePath(get("virtual.engines.openesb_v.deploymentFile"));
        operation.setDeploymentDir(get("virtual.engines.openesb_v.deploymentDir"));
        operation.setDeployTimeout(Integer.parseInt(get("virtual.engines.openesb_v.deploymentTimeout")));

        return operation;
    }

    @Override
    public LogFilesRequest buildLogFilesRequest() {
        LogFilesRequest request = new LogFilesRequest();
        request.getPaths().add(get("virtual.engines.openesb_v.bvmsDir") + "/log");
        request.getPaths().add(get("virtual.engines.openesb_v.logfileDir"));
        return request;
    }

    @Override
    public boolean getHeadlessModeOption() {
        return Boolean.valueOf(Configuration.get("virtual.engines.openesb_v.headless"));
    }

    @Override
    public boolean saveStateInsteadOfShutdown() {
        return Boolean.valueOf(Configuration.get("virtual.engines.openesb_v.shutdownSaveState"));
    }
}
