package betsy.virtual.host.engines;

import betsy.Configuration;
import betsy.data.BetsyProcess;
import betsy.data.engines.ode.OdeEngine;
import betsy.virtual.host.ServiceAddress;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class VirtualizedOdeEngine extends VirtualizedEngine {

    public VirtualizedOdeEngine() {
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
        saList.add(new ServiceAddress("http://localhost:8080/ode"));
        return saList;
    }

    @Override
    public String getEndpointUrl(BetsyProcess process) {
        // is not delegated because of the dependency to the local Tomcat
        return "http://localhost:8080/ode/processes/"
                + process.getBpelFileNameWithoutExtension() + "TestInterface";
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(8080);
        return portList;
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
    public String getVMDeploymentDir() {
        return Configuration.get("virtualisation.engines.ode_v.deploymentDir");
    }

    @Override
    public String getVMLogfileDir() {
        return Configuration.get("virtualisation.engines.ode_v.logfileDir");
    }

    @Override
    public Path getDeployableFilePath(BetsyProcess process) {
        return Paths.get(process.getTargetPackageFilePath("zip"));
    }

    @Override
    public String getVMbVMSDir() {
        return Configuration.get("virtualisation.engines.ode_v.bvmsDir") + "/log";
    }
}
