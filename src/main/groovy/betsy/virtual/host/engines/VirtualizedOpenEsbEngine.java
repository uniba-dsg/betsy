package betsy.virtual.host.engines;

import betsy.Configuration;
import betsy.data.BetsyProcess;
import betsy.data.engines.openesb.OpenEsbEngine;
import betsy.virtual.common.messages.DeployOperation;
import betsy.virtual.common.messages.FileMessage;
import betsy.virtual.host.ServiceAddress;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class VirtualizedOpenEsbEngine extends VirtualizedEngine {

    private final OpenEsbEngine defaultEngine;

    public VirtualizedOpenEsbEngine() {
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
        saList.add(new ServiceAddress("http://localhost:8383/"));
        saList.add(new ServiceAddress("http://localhost:4848/"));
        return saList;
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        // BPEL services
        portList.add(18181);
        // HTTP
        portList.add(8383);
        // HTTPS
        portList.add(8384);
        // ADMIN
        portList.add(4848);
        return portList;
    }

    @Override
    public String getEndpointUrl(BetsyProcess process) {
        return defaultEngine.getEndpointUrl(process);
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

    @Override
    public void onPostDeployment(BetsyProcess process) {
        // not required. deploy is in sync and does not return before process is
        // deployed
    }

    @Override
    public DeployOperation buildDeployOperation(BetsyProcess process)
            throws IOException {
        Path path = getDeployableFilePath(process);
        Path filenamePath = path.getFileName();
        String filename = filenamePath.toString();
        byte[] data = Files.readAllBytes(path);

        DeployOperation operation = new DeployOperation();
        FileMessage fm = new FileMessage(filename, data);
        operation.setFileMessage(fm);
        operation.setEngineName(getName());
        operation.setBpelFileNameWithoutExtension(process
                .getBpelFileNameWithoutExtension());
        operation.setEngineLogDir(getVMLogfileDir());
        operation.setDeploymentFile(getVMDeploymentFile());
        operation.setDeployTimeout(getVMDeploymentTimeout());

        return operation;
    }

    @Override
    public String getVMDeploymentDir() {
        // no deployment dir, openesb requires deployment executable
        return null;
    }

    @Override
    public String getVMLogfileDir() {
        return Configuration.getValueAsString(
                "virtualisation.engines.openesb_v.logfileDir");
    }

    public String getVMDeploymentFile() {
        return Configuration.getValueAsString(
                "virtualisation.engines.openesb_v.deploymentFile");
    }

    @Override
    public Path getDeployableFilePath(BetsyProcess process) {
        return Paths.get(process.getTargetPackageCompositeFilePath());
    }

    @Override
    public String getVMbVMSDir() {
        String bVMSDir = Configuration.getValueAsString(
                "virtualisation.engines.openesb_v.bvmsDir");
        bVMSDir = bVMSDir.endsWith("/") ? bVMSDir : bVMSDir + "/";
        bVMSDir += "log";
        return bVMSDir;
    }

}
