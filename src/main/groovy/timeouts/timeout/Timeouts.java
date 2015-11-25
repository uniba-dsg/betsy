package timeouts.timeout;

import org.apache.log4j.Logger;
import timeouts.TimeoutIOOperations;

import java.io.File;
import java.util.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Timeouts {

    private static final Logger LOGGER = Logger.getLogger(Timeouts.class);
    private List<Timeout> timeouts = new ArrayList<>();
    private File properties = new File("timeout.properties");
    private File csv = new File ("timeouts.csv");

    /**
     *
     * @param timeouts The timeouts, which should be managed by the {@link timeouts}.
     * @param nameOfProperties The name of the properties file.
     * @param nameOfCSV The name of the csv file.
     */
    public Timeouts(ArrayList<Timeout> timeouts, String nameOfProperties, String nameOfCSV) {
        if (nameOfProperties != null && nameOfProperties.length() > 0) {
            this.properties = new File(nameOfProperties);
        }
        if (nameOfCSV != null && nameOfCSV.length() > 0) {
            this.csv = new File(nameOfCSV + ".csv");
        }
        if(timeouts != null){
            this.timeouts = timeouts;
        }
    }

    /**
     * @param nameOfProperties The name of the properties file.
     * @param nameOfCSV The name of the csv file.
     */
    public Timeouts(String nameOfProperties, String nameOfCSV) {
        if (nameOfProperties != null && nameOfProperties.length() > 0) {
            this.properties = new File(nameOfProperties);
        }
        if (nameOfCSV != null && nameOfCSV.length() > 0) {
            this.csv = new File(nameOfCSV + ".csv");
        }
        addTimeouts();
    }

    /**
     *
     * @param timeouts The timeouts, which should be managed by the {@link timeouts}.
     */
    public Timeouts(ArrayList<Timeout> timeouts) {
        if(timeouts != null){
            this.timeouts = timeouts;
        }
    }

    /**
     *
     */
    public Timeouts() {
        addTimeouts();
    }

    /**
     * This method returns the {@link Timeout} for given key as {@link Optional}.
     *
     * @param key The key of the {@link Timeout}.
     * @return The {@link Timeout] for the given key as {@link Optional}.
     */
    public Optional<Timeout> getTimeout(String key) {
        return Optional.ofNullable(getAllTimeouts().get(key));
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
     *
     * This method it is possible to change the values of an existing {@link Timeout}.
     *
     * @param timeout The {@link Timeout} with the new values.
     */
    public void setTimeout(Timeout timeout){
        if (getTimeout(timeout.getKey()).isPresent()) {
            getTimeout(timeout.getKey()).get().setValue(timeout.getTimeoutInMs());
            getTimeout(timeout.getKey()).get().setTimeToRepetition(timeout.getTimeToRepetitionInMs());
        } else {
            LOGGER.info("The timeout(" + timeout.getKey() + ") does not exist.");
        }
    }


    /**
     * This method writes all given {@link Timeout} to the properties.
     */
    public void writeAllTimeoutsToProperties() {
        TimeoutIOOperations.writeToProperties(properties, timeouts);
    }


    /**
     * This method reads all timeouts, which are set in the {@link Timeouts} from the {@link Properties}.
     */
    public void readTimeoutProperties() {
        timeouts = TimeoutIOOperations.readFromProperties(properties, timeouts);
    }

    /**
     *
     */
    public void writeToCSV() {
        TimeoutIOOperations.writeToCSV(csv, timeouts);
    }

    private void addTimeouts(){
        timeouts.add(new Timeout("Tomcat", "startup", 30000, 500));
        timeouts.add(new Timeout("Comunda", "deploy", 20000, 500));
        timeouts.add(new Timeout("Comunda", "startup", 30000, 500));
        timeouts.add(new Timeout("Comunda710", "startup", 30000, 500));
        timeouts.add(new Timeout("Jbpm", "deploy", "maven", 1500));
        timeouts.add(new Timeout("Jbpm", "deploy", "result", 30000, 1000));
        timeouts.add(new Timeout("Jbpm", "deploy", "availability", 5000));
        timeouts.add(new Timeout("Jbpm", "startup", 240000, 5000));
        timeouts.add(new Timeout("Jbpm", "shutdon", 240000, 5000));
        timeouts.add(new Timeout("JbpmTester", "runTest", 10000));

        timeouts.add(new Timeout("EventProcesses", "SIGNAL_INTERMEDIATE_EVENT_THROW_AND_CATCH", 10000));
        timeouts.add(new Timeout("EventProcesses", "TIMER_INTERMEDIATE_EVENT", 5000));
        timeouts.add(new Timeout("EventProcesses", "TIMER_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING", 5000));
        timeouts.add(new Timeout("EventProcesses", "TIMER_BOUNDARY_EVENT_SUBPROCESS_TIMECYCLE", 65000));
        timeouts.add(new Timeout("EventProcesses", "TIMER_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING", 5000));
        timeouts.add(new Timeout("EventProcesses", "TIMER_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING_ACTIVITIY", 12000));
        timeouts.add(new Timeout("EventProcesses", "TIMER_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING", 5000));
        timeouts.add(new Timeout("EventProcesses", "TIMER_START_EVENT_TIMECYCLE_EVENT_SUBPROCESS_NON_INTERRUPTING", 35000));
        timeouts.add(new Timeout("EventProcesses", "TIMER_INTERMEDIATE_TIMECYCLE_EVENT", 5000));
        timeouts.add(new Timeout("EventProcesses", "TIMER_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING", 5000));
        timeouts.add(new Timeout("GatewayProcesses", "PARALLEL_GATEWAY_TRUE_PARALLELISM", 22000));
        timeouts.add(new Timeout("GatewayProcesses", "EVENT_BASED_GATEWAY_SIGNALS", 10000));
        timeouts.add(new Timeout("GatewayProcesses", "EVENT_BASED_GATEWAY_TIMER", 5000));


        //BPEL
        timeouts.add(new Timeout("Retry", "atMostThreeTimes", 500));
        timeouts.add(new Timeout("BPELComposite", "test", 2000));
        timeouts.add(new Timeout("BPELComposite", "testSoapUi", 500));
        timeouts.add(new Timeout("TestingAPI", "constructor", 15000));

        timeouts.add(new Timeout("ActiveBpelDeployer", "constructor", 100000, 500));
        timeouts.add(new Timeout("BpelgDeployer", "deploy", 100000, 500));
        timeouts.add(new Timeout("BpelgDeployer", "undeploy", 100000, 500));
        timeouts.add(new Timeout("OdeDeployer", "deploy", 30000, 500));
        timeouts.add(new Timeout("OdeDeployer", "undeploy", 30000, 500));
        timeouts.add(new Timeout("OpenEsb301StandaloneEngine", "installComponent", 10000, 500));
        timeouts.add(new Timeout("OpenEsb301StandaloneEngine", "startup", "waitForUrl", 10000, 500));
        timeouts.add(new Timeout("OpenEsb301StandaloneEngine", "startup", "waitForStart", 10000, 500));
        timeouts.add(new Timeout("OpenEsbEngine", "startup", 15000, 500));
        timeouts.add(new Timeout("PetalsEsbDeployer", "deploy", 20000, 500));
        timeouts.add(new Timeout("PetalsEsbDeployer", "undeploy", 20000, 500));
        timeouts.add(new Timeout("PetalsEsbEngine", "startup", 30000, 500));
        timeouts.add(new Timeout("Wso2Deployer", "deploy", "waitFor", 120000, 500));
        timeouts.add(new Timeout("Wso2Deployer", "deploy", "sleep", 2000));
        timeouts.add(new Timeout("Wso2Deployer", "undeploy", "waitFor", 120000, 500));
        timeouts.add(new Timeout("Wso2Deployer", "undeploy", "sleep", 1000));
        timeouts.add(new Timeout("Wso2Engine_v3_1_0", "startup", "sleep", 2000));
        timeouts.add(new Timeout("Wso2Engine_v3_1_0", "startup", "waitFor", 120000, 500));

        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_CORRELATION_INIT_ASYNC", "1", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_CORRELATION_INIT_ASYNC", "2", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_CORRELATION_INIT_SYNC", "1", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_CORRELATION_INIT_SYNC", "2", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_AMBIGUOUS_RECEIVE_FAULT", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_CONFLICTING_RECEIVE_FAULT", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_REPLY_CONFLICTING_REQUEST_FAULT", "1", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_REPLY_CONFLICTING_REQUEST_FAULT", "2", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_REPLY_CORRELATION_INIT_ASYNC", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_REPLY_CORRELATION_INIT_SYNC", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "RECEIVE_REPLY_CORRELATION_VIOLATION_YES", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "INVOKE_CORRELATION_PATTERN_INIT_ASYNC", 1000));
        timeouts.add(new Timeout("BasicActivityProcesses", "INVOKE_CORRELATION_PATTERN_INIT_SYNC", 1000));
        timeouts.add(new Timeout("PatternProcesses", "MILESTONE_PATTERN", 4000));
        timeouts.add(new Timeout("ScopeProcesses", "SCOPE_EVENT_HANDLER_ASYNC_INIT_SYNC", 3000));
        timeouts.add(new Timeout("ScopeProcesses", "SCOPE_EVENT_HANDLER_INIT_SYNC", 3000));
        timeouts.add(new Timeout("ScopeProcesses", "SCOPE_EVENT_HANDLER_FLOW_INIT_SYNC", 3000));
        timeouts.add(new Timeout("ScopeProcesses", "SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_SYNC", 3000));
        timeouts.add(new Timeout("ScopeProcesses", "SCOPE_EVENT_HANDLER_INTERNAL_MESSAGE_EXCHANGE_INIT_SYNC", 3000));
        timeouts.add(new Timeout("ScopeProcesses", "SCOPE_EVENT_HANDLER_SCOPE_MESSAGE_EXCHANGE_INIT_SYNC", 3000));
        timeouts.add(new Timeout("ScopeProcesses", "SCOPE_EVENT_HANDLER_ELEMENT_INIT_SYNC", 3000));
    }
}
