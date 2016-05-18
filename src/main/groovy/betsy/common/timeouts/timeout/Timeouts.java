package betsy.common.timeouts.timeout;

import betsy.common.timeouts.Properties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Timeouts {

    private List<Timeout> timeouts = new ArrayList<>();
    private Path properties = Paths.get("timeout.properties");

    /**
     * @param timeouts         The timeouts, which should be managed by the {@link betsy.common.timeouts}.
     * @param nameOfProperties The name of the properties file.
     */
    public Timeouts(ArrayList<Timeout> timeouts, String nameOfProperties) {
        Objects.requireNonNull(nameOfProperties, "nameOfProperties can't be null.");
        if (nameOfProperties.length() > 0) {
            this.properties = Paths.get(nameOfProperties);
        }
        this.timeouts = Objects.requireNonNull(timeouts, "The timeouts can't be null.");
    }

    /**
     * @param nameOfProperties The name of the properties file.
     */
    public Timeouts(String nameOfProperties) {
        Objects.requireNonNull(nameOfProperties, "nameOfProperties can't be null.");
        if (nameOfProperties.length() > 0) {
            this.properties = Paths.get(nameOfProperties);
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
     * This method reads all timeouts, which are set in the {@link Timeouts} from the {@link betsy.common.timeouts.Properties}.
     */
    public void readTimeoutProperties() {
        timeouts = Properties.read(properties, timeouts);
    }

    private void addTimeouts() {
        timeouts.add(new Timeout("Tomcat.startup", 30_000, 500));
        timeouts.add(new Timeout("FileTasks.deleteDirectory", 5_000, Timeout.Category.UNMEASURABLE));

        //Virtual engines
        timeouts.add(new Timeout("active_bpel_v.service", 30_000, 500));
        timeouts.add(new Timeout("bpelg_v.service", 30_000, 500));
        timeouts.add(new Timeout("ode_v.service", 30_000, 500));
        timeouts.add(new Timeout("openesb_v.service", 30_000, 500));
        timeouts.add(new Timeout("orchestra_v.service", 30_000, 500));
        timeouts.add(new Timeout("petalsesb_v.service", 30_000, 500));

        timeouts.add(new Timeout("active_bpel_v.deployment", 30_000, 500));
        timeouts.add(new Timeout("bpelg_v.deployment", 30_000, 500));
        timeouts.add(new Timeout("ode_v.deployment", 30_000, 500));
        timeouts.add(new Timeout("openesb_v.deployment", 30_000, 500));
        timeouts.add(new Timeout("orchestra_v.deployment", 30_000, 500));
        timeouts.add(new Timeout("petalsesb_v.deployment", 30_000, 500));

        timeouts.add(new Timeout("PortForwardingConfigurator.applyPortForwarding", 10_000, 100));
        timeouts.add(new Timeout("PortForwardingConfigurator.clearPortForwarding", 10_000, 100));

        timeouts.add(new Timeout("VirtualBoxMachineImpl.startVirtualMachine", 60_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl.saveState", 30_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl.restore", 30_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl.stop", 10_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl.takeSnapshot", 30_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VirtualBoxMachineImpl.takeSnapshot.sleep", 500, Timeout.Category.UNMEASURABLE));

        timeouts.add(new Timeout("SnapshotCreator.invoke", 30_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("SnapshotCreator.failIfBetsyServerTimesOut", 15_000, Timeout.Category.UNMEASURABLE));

        timeouts.add(new Timeout("VBoxApplianceImporter.importAppliance.readProgress", 1_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("VBoxApplianceImporter.importAppliance.importProgress", 1_000, Timeout.Category.UNMEASURABLE));

        //BPMN
        timeouts.add(new Timeout("Camunda.deploy", 20_000, 500));
        timeouts.add(new Timeout("Camunda.startup", 30_000, 500));
        timeouts.add(new Timeout("Camunda710.startup", 30_000, 500));
        timeouts.add(new Timeout("Jbpm.deploy.maven", 1500));
        timeouts.add(new Timeout("Jbpm.deploy.result", 30_000, 1000));
        timeouts.add(new Timeout("Jbpm.deploy.availability", 5000));
        timeouts.add(new Timeout("Jbpm.startup", 24_0000, 5000));
        timeouts.add(new Timeout("Jbpm.shutdown", 24_0000, 5000));
        timeouts.add(new Timeout("JbpmTester.runTest", 10_000));

        //BPEL
        timeouts.add(new Timeout("Retry.atMostThreeTimes", 500, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("BPELComposite.test", 2_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("BPELComposite.testSoapUi", 500, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("TestingAPI.generateEngineDependentTest", 15_000, Timeout.PlaceOfUse.EXTERN));

        timeouts.add(new Timeout("ActiveBpel.deploy", 100_000, 500));
        timeouts.add(new Timeout("Bpelg.deploy", 100_000, 500));
        timeouts.add(new Timeout("Ode.deploy", 30_000, 500));
        timeouts.add(new Timeout("OpenEsb30x.installComponent", 10_000, 500));
        timeouts.add(new Timeout("OpenEsb30x.startup.waitForUrl", 10_000, 500));
        timeouts.add(new Timeout("OpenEsb30x.startup.waitForStart", 10_000, 500));
        timeouts.add(new Timeout("OpenEsb.startup", 15_000, 500));
        timeouts.add(new Timeout("PetalsEsb.deploy", 20_000, 500));
        timeouts.add(new Timeout("PetalsEsb.startup", 30_000, 500));
        timeouts.add(new Timeout("Wso2.deploy", 120_000, 500));
        timeouts.add(new Timeout("Wso2.deploy.sleep", 2_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("Wso2.undeploy", 120_000, 500));
        timeouts.add(new Timeout("Wso2.undeploy.sleep", 1_000, Timeout.Category.UNMEASURABLE));
        timeouts.add(new Timeout("Wso2_v3_1_0.startup", 120_000, 500));
        timeouts.add(new Timeout("Wso2_v3_1_0.startup.sleep", 2_000, Timeout.Category.UNMEASURABLE));
    }
}

