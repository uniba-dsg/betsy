package betsy.virtual.host.engines;

import betsy.data.BetsyProcess;
import betsy.data.engines.ode.OdeEngine;
import betsy.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.virtual.common.messages.deploy.DeployRequest;
import betsy.virtual.common.messages.deploy.FileMessage;
import betsy.virtual.host.ServiceAddress;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static betsy.Configuration.get;
import static betsy.Configuration.getValueAsInteger;

public class VirtualOdeEngine extends VirtualEngine {
    public static final int HTTP_PORT = 8080;

    // TODO each engine has different logic and different files. on message per engine? or a general one?
    // TODO FileMessage + Deployer with correctly prefilled Attributes?

    public VirtualOdeEngine() {
        super();
        this.defaultEngine = new OdeEngine();
    }

    @Override
    public String getName() {
        return "ode_v";
    }

    @Override
    public List<ServiceAddress> getVerifiableServiceAddresses() {
        List<ServiceAddress> saList = new LinkedList<>();
        saList.add(new ServiceAddress("http://localhost:" + HTTP_PORT + "/ode"));
        return saList;
    }

    @Override
    public String getEndpointUrl(BetsyProcess process) {
        // is not delegated because of the dependency to the local Tomcat
        return "http://localhost:" + HTTP_PORT + "/ode/processes/" + process.getName() + "TestInterface";
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(HTTP_PORT);
        return portList;
    }

    @Override
    public DeployRequest buildDeployRequest(BetsyProcess process) throws IOException {
        DeployRequest operation = new DeployRequest();
        operation.setFileMessage(FileMessage.build(process.getTargetPackageFilePath("zip")));
        operation.setEngineName(getName());
        operation.setProcessName(process.getName());
        operation.setDeploymentLogFilePath(get("virtual.engines.ode_v.deploymentLogFile"));
        operation.setDeploymentDir(get("virtual.engines.ode_v.deploymentDir"));
        operation.setDeployTimeout(getValueAsInteger("virtual.engines.ode_v.deploymentTimeout"));

        return operation;
    }

    @Override
    public LogFilesRequest buildLogFilesRequest(BetsyProcess process) {
        LogFilesRequest request = new LogFilesRequest();
        request.getPaths().add(get("virtual.engines.ode_v.bvmsDir") + "/log");
        request.getPaths().add(get("virtual.engines.ode_v.logfileDir"));
        return request;
    }

    @Override
    public void buildArchives(BetsyProcess process) {
        // use default engine's operations
        defaultEngine.buildArchives(process);
    }

    @Override
    public String getXsltPath() {
        return defaultEngine.getXsltPath();
    }

}
