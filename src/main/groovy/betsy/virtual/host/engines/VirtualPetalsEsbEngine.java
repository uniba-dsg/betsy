package betsy.virtual.host.engines;

import betsy.config.Configuration;
import betsy.data.BetsyProcess;
import betsy.data.engines.petalsesb.PetalsEsbEngine;
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

public class VirtualPetalsEsbEngine extends VirtualEngine {

    public static final int HTTP_PORT = 8084;

    public VirtualPetalsEsbEngine() {
        super();
        this.defaultEngine = new PetalsEsbEngine();
    }

    @Override
    public String getName() {
        return "petalsesb_v";
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
        operation.setDeploymentLogFilePath(get("virtual.engines.petalsesb_v.deploymentLogFile"));
        operation.setDeploymentDir(get("virtual.engines.petalsesb_v.deploymentDir"));
        operation.setDeployTimeout(Integer.parseInt(get("virtual.engines.petalsesb_v.deploymentTimeout")));

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
        return Boolean.valueOf(Configuration.get("virtual.engines.petalsesb_v.headless"));
    }

    @Override
    public boolean saveStateInsteadOfShutdown() {
        return Boolean.valueOf(Configuration.get("virtual.engines.petalsesb_v.shutdownSaveState"));
    }

}
