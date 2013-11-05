package betsy.virtual.host.engines;

import betsy.Configuration;
import betsy.data.BetsyProcess;
import betsy.data.engines.petalsesb.PetalsEsbEngine;
import betsy.virtual.host.ServiceAddress;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class VirtualizedPetalsEsbEngine extends VirtualizedEngine {

    private final PetalsEsbEngine defaultEngine;

    public VirtualizedPetalsEsbEngine() {
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
        saList.add(new ServiceAddress(
                "http://localhost:8084/petals/services/listServices"));
        return saList;
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(8084);
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
    public String getVMDeploymentDir() {
        return Configuration.getValueAsString(
                "virtualisation.engines.petalsesb_v.deploymentDir");
    }

    @Override
    public String getVMLogfileDir() {
        return Configuration.getValueAsString(
                "virtualisation.engines.petalsesb_v.logfileDir");
    }

    @Override
    public Path getDeployableFilePath(BetsyProcess process) {
        return Paths.get(process.getTargetPackageCompositeFilePath());
    }

    @Override
    public String getVMbVMSDir() {
        String bVMSDir = Configuration.getValueAsString(
                "virtualisation.engines.petalsesb_v.bvmsDir");
        bVMSDir = bVMSDir.endsWith("/") ? bVMSDir : bVMSDir + "/";
        bVMSDir += "log";
        return bVMSDir;
    }
}
