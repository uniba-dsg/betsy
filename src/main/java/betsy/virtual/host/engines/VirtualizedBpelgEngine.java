package betsy.virtual.host.engines;

import betsy.Configuration;
import betsy.data.BetsyProcess;
import betsy.data.engines.bpelg.BpelgEngine;
import betsy.virtual.host.ServiceAddress;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class VirtualizedBpelgEngine extends VirtualizedEngine {

    private final Configuration config = Configuration.getInstance();

    public VirtualizedBpelgEngine() {
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
        saList.add(new ServiceAddress("http://localhost:8080/bpel-g/services"));
        return saList;
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(8080);
        return portList;
    }

    @Override
    public String getEndpointUrl(BetsyProcess process) {
        // is not delegated because of the dependency to the local Tomcat
        return "http://localhost:8080/bpel-g/services/"
                + process.getBpelFileNameWithoutExtension()
                + "TestInterfaceService";
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
        return config.getValueAsString(
                "virtualisation.engines.bpelg_v.deploymentDir",
                "/usr/share/tomcat7/bpr");
    }

    @Override
    public String getVMLogfileDir() {
        return config.getValueAsString(
                "virtualisation.engines.bpelg_v.logfileDir",
                "/var/lib/tomcat7/logs");
    }

    @Override
    public Path getDeployableFilePath(BetsyProcess process) {
        return Paths.get(process.getTargetPackageFilePath("zip"));
    }

    @Override
    public String getVMbVMSDir() {
        String bVMSDir = config.getValueAsString(
                "virtualisation.engines.bpelg_v.bvmsDir", "/opt/betsy/");
        bVMSDir = bVMSDir.endsWith("/") ? bVMSDir : bVMSDir + "/";
        bVMSDir += "log";
        return bVMSDir;
    }
}
