package configuration;

import java.util.Arrays;

import pebl.feature.Capability;
import pebl.feature.Metric;
import pebl.feature.ValueType;

public class Capabilities {

    public static final Capability CONFORMANCE = new Capability("Conformance", Arrays.asList(
            new Metric(ValueType.LONG, "testCases", "The number of test cases.", "quantity", "Conformance"),
            new Metric(ValueType.LONG, "testCaseSuccesses", "The number of successful test cases.", "quantity", "Conformance"),
            new Metric(ValueType.LONG, "testCaseFailures", "The number of failed test cases.", "quantity", "Conformance"),
            new Metric(ValueType.BOOLEAN, "testDeployable", "Whether the underlying process model was deployed successfully.", "", "Conformance"),
            new Metric(ValueType.BOOLEAN, "testSuccessful", "Whether all test cases were successfully.", "", "Conformance"),
            new Metric(ValueType.STRING, "testResult", "The overall result based on the number of successful test cases compared to the total number of test cases", "", "Conformance"),
            new Metric(ValueType.LONG, "executionDuration", "The time the execution of this particular test.", "milliseconds", "Conformance"),
            new Metric(ValueType.LONG, "executionTimestamp", "The time the execution of this particular test has been started.", "timestamp", "Conformance")
    ));

    public static final Capability EXPRESSIVENESS = new Capability("Expressiveness", Arrays.asList(
            new Metric(ValueType.LONG, "testCases", "The number of test cases.", "quantity", "Expressiveness"),
            new Metric(ValueType.LONG, "testCaseSuccesses", "The number of successful test cases.", "quantity", "Expressiveness"),
            new Metric(ValueType.LONG, "testCaseFailures", "The number of failed test cases.", "quantity", "Expressiveness"),
            new Metric(ValueType.BOOLEAN, "testDeployable", "Whether the underlying process model was deployed successfully.", "", "Expressiveness"),
            new Metric(ValueType.BOOLEAN, "testSuccessful", "Whether all test cases were successfully.", "", "Expressiveness"),
            new Metric(ValueType.STRING, "testResult", "The overall result based on the number of successful test cases compared to the total number of test cases", "", "Expressiveness"),
            new Metric(ValueType.LONG, "executionDuration", "The time the execution of this particular test.", "milliseconds", "Expressiveness"),
            new Metric(ValueType.LONG, "executionTimestamp", "The time the execution of this particular test has been started.", "timestamp", "Expressiveness")
    ));

    public static final Capability ROBUSTNESS = new Capability("Robustness", Arrays.asList(
            new Metric(ValueType.LONG, "testCases", "The number of test cases.", "quantity", "Robustness"),
            new Metric(ValueType.LONG, "testCaseSuccesses", "The number of successful test cases.", "quantity", "Robustness"),
            new Metric(ValueType.LONG, "testCaseFailures", "The number of failed test cases.", "quantity", "Robustness"),
            new Metric(ValueType.BOOLEAN, "testDeployable", "Whether the underlying process model was deployed successfully.", "", "Robustness"),
            new Metric(ValueType.BOOLEAN, "testSuccessful", "Whether all test cases were successfully.", "", "Robustness"),
            new Metric(ValueType.STRING, "testResult", "The overall result based on the number of successful test cases compared to the total number of test cases", "", "Robustness"),
            new Metric(ValueType.LONG, "executionDuration", "The time the execution of this particular test.", "milliseconds", "Robustness"),
            new Metric(ValueType.LONG, "executionTimestamp", "The time the execution of this particular test has been started.", "timestamp", "Robustness")
    ));

    public static final Capability PERFORMANCE = new Capability("Performance", Arrays.asList(
            new Metric(ValueType.AGGREGATED, "process_duration", "The duration of the process instances.", "seconds", "Performance"),
            new Metric(ValueType.AGGREGATED, "throughput", "The number of process instances completed per second.", "process instances/second", "Performance"),
            new Metric(ValueType.AGGREGATED, "number_of_process_instances", "The number of process instances.", "quantity", "Performance"),
            new Metric(ValueType.AGGREGATED, "cpu", "The CPU utilization in percentage.", "%", "Performance"),
            new Metric(ValueType.AGGREGATED, "ram", "The RAM utilization in MegaByte.", "MB", "Performance"),
            new Metric(ValueType.AGGREGATED, "size_of_stored_data", "The size of data stored in the DBMS in MegaByte.", "MB", "Performance"),
            new Metric(ValueType.AGGREGATED, "io", "The IO utilization in MegaByte.", "MB", "Performance"),
            new Metric(ValueType.LONG, "executionDuration", "The time the execution of this particular test.", "milliseconds", "Performance"),
            new Metric(ValueType.LONG, "executionTimestamp", "The time the execution of this particular test has been started.", "timestamp", "Performance")
    ));

}
