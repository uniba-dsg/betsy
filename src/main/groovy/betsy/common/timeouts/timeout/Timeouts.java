package betsy.common.timeouts.timeout;

import betsy.common.timeouts.TimeoutIOOperations;

import java.io.File;
import java.util.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Timeouts {

    private List<Timeout> timeouts = new ArrayList<>();
    private File properties = new File("timeout.properties");

    /**
     * @param timeouts         The timeouts, which should be managed by the {@link betsy.common.timeouts}.
     * @param nameOfProperties The name of the properties file.
     */
    public Timeouts(ArrayList<Timeout> timeouts, String nameOfProperties) {
        Objects.requireNonNull(nameOfProperties, "nameOfProperties can't be null.");
        if (nameOfProperties.length() > 0) {
            this.properties = new File(nameOfProperties);
        }
        this.timeouts = Objects.requireNonNull(timeouts, "The timeouts can't be null.");
    }

    /**
     * @param nameOfProperties The name of the properties file.
     */
    public Timeouts(String nameOfProperties) {
        Objects.requireNonNull(nameOfProperties, "nameOfProperties can't be null.");
        if (nameOfProperties.length() > 0) {
            this.properties = new File(nameOfProperties);
        }
        addTimeouts();
    }

    /**
     * @param timeouts The timeouts, which should be managed by the {@link betsy.common.timeouts}.
     */
    public Timeouts(ArrayList<Timeout> timeouts) {
        this.timeouts = Objects.requireNonNull(timeouts, "The timeouts can't be null.");
    }

    /**
     *
     */
    public Timeouts() {
        addTimeouts();
    }

    /**
     * This method returns the {@link Timeout} for given key.
     *
     * @param key The key of the {@link Timeout}.
     * @return The {@link Timeout] for the given key.
     */
    public Timeout getTimeout(String key) {
        Objects.requireNonNull(key, "The key can't be null.");
        if (getAllTimeouts().get(key) != null) {
            return getAllTimeouts().get(key);
        } else {
            throw new NoSuchElementException("There is no timeout with this given key.");
        }
    }

    /**
     * This method returns a {@link HashMap} with all Timeouts as {@link HashMap}.
     *
     * @return A {@link HashMap} with the key as {@link String} and a {@link Timeout}.
     */
    public HashMap<String, Timeout> getAllTimeouts() {
        HashMap<String, Timeout> map = new HashMap<>();
        for (Timeout timeout : timeouts) map.put(timeout.getKey(), timeout);
        return map;
    }

    /**
     * This method it is possible to change the values of an existing {@link Timeout}.
     *
     * @param timeout The {@link Timeout} with the new values.
     */
    public void setTimeout(Timeout timeout) {
        Objects.requireNonNull(timeout, "The timeout can't be null.");
        getTimeout(timeout.getKey()).setValue(timeout.getTimeoutInMs());
        getTimeout(timeout.getKey()).setTimeToRepetition(timeout.getTimeToRepetitionInMs());
    }

    /**
     * This method reads all timeouts, which are set in the {@link Timeouts} from the {@link Properties}.
     */
    public void readTimeoutProperties() {
        timeouts = TimeoutIOOperations.readFromProperties(properties, timeouts);
    }

    private void addTimeouts() {
        timeouts.add(new Timeout("Tomcat", "startup", 30_000, 500));
        timeouts.add(new Timeout("FileTasks", "deleteDirectory", 5_000, Timeout.Category.UNMEASURABLE));

        //Virtual engines
        timeouts.add(new Timeout("active_bpel_v", "serviceTimeout", 30_000, 500));
        timeouts.add(new Timeout("bpelg_v", "serviceTimeout", 30_000, 500));
        timeouts.add(new Timeout("ode_v", "serviceTimeout", 30_000, 500));
        timeouts.add(new Timeout("openesb_v", "serviceTimeout", 30_000, 500));
        timeouts.add(new Timeout("orchestra_v", "serviceTimeout", 30_000, 500));
        timeouts.add(new Timeout("petalsesb_v", "serviceTimeout", 30_000, 500));

        timeouts.add(new Timeout("active_bpel_v", "deploymentTimeout", 30_000, 500));
        timeouts.add(new Timeout("bpelg_v", "deploymentTimeout", 30_000, 500));
        timeouts.add(new Timeout("ode_v", "deploymentTimeout", 30_000, 500));
        timeouts.add(new Timeout("openesb_v", "deploymentTimeout", 30_000, 500));
        timeouts.add(new Timeout("orchestra_v", "deploymentTimeout", 30_000, 500));
        timeouts.add(new Timeout("petalsesb_v", "deploymentTimeout", 30_000, 500));

        timeouts.add(new Timeout("PortForwardingConfigurator", "applyPortForwarding", 10_000, 100));
        timeouts.add(new Timeout("PortForwardingConfigurator", "clearPortForwarding", 10_000, 100));

        timeouts.add(new Timeout("VirtualBoxMachineImpl", "startVirtualMachine", 60_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl", "saveState", 30_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl", "restore", 30_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl", "stop", 10_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl", "takeSnapshot", 30_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl", "takeSnapshot", "sleep", 500, Timeout.Category.UNMEASURABLE));

        timeouts.add(new Timeout("SnapshotCreator", "invoke", 30_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("SnapshotCreator", "failIfBetsyServerTimesOut", 15_000, Timeout.Category.UNMEASURABLE));

        timeouts.add(new Timeout("VBoxApplianceImporter", "importAppliance", "readProgress", 1_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VBoxApplianceImporter", "importAppliance", "importProgress", 1_000, Timeout.Category.UNMEASURABLE));

        //BPMN
        timeouts.add(new Timeout("Camunda", "deploy", 20_000, 500));
        timeouts.add(new Timeout("Camunda", "startup", 30_000, 500));
        timeouts.add(new Timeout("Camunda710", "startup", 30_000, 500));
        timeouts.add(new Timeout("Jbpm", "deploy", "maven", 1500));
        timeouts.add(new Timeout("Jbpm", "deploy", "result", 30_000, 1000));
        timeouts.add(new Timeout("Jbpm", "deploy", "availability", 5000));
        timeouts.add(new Timeout("Jbpm", "startup", 24_0000, 5000));
        timeouts.add(new Timeout("Jbpm", "shutdown", 24_0000, 5000));
        timeouts.add(new Timeout("JbpmTester", "runTest", 10_000));

        //BPEL
        timeouts.add(new Timeout("Retry", "atMostThreeTimes", 500, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("BPELComposite", "test", 2_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("BPELComposite", "testSoapUi", 500, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("TestingAPI", "constructor", 15_000, Timeout.PlaceOfUse.EXTERN));

        timeouts.add(new Timeout("ActiveBpelDeployer", "constructor", 100_000, 500));
        timeouts.add(new Timeout("BpelgDeployer", "constructor", 100_000, 500));
        timeouts.add(new Timeout("OdeDeployer", "constructor", 30_000, 500));
        timeouts.add(new Timeout("OpenEsb301StandaloneEngine", "installComponent", 10_000, 500));
        timeouts.add(new Timeout("OpenEsb301StandaloneEngine", "startup", "waitForUrl", 10_000, 500));
        timeouts.add(new Timeout("OpenEsb301StandaloneEngine", "startup", "waitForStart", 10_000, 500));
        timeouts.add(new Timeout("OpenEsbEngine", "startup", 15_000, 500));
        timeouts.add(new Timeout("PetalsEsbDeployer", "constructor", 20_000, 500));
        timeouts.add(new Timeout("PetalsEsbEngine", "startup", 30_000, 500));
        timeouts.add(new Timeout("Wso2Deployer", "deploy", 120_000, 500));
        timeouts.add(new Timeout("Wso2Deployer", "deploy", "sleep", 2_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("Wso2Deployer", "undeploy", 120_000, 500));
        timeouts.add(new Timeout("Wso2Deployer", "undeploy", "sleep", 1_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("Wso2Engine_v3_1_0", "startup", 120_000, 500));
        timeouts.add(new Timeout("Wso2Engine_v3_1_0", "startup", "sleep", 2_000, Timeout.Category.UNMEASURABLE));
    }
}

