package configuration;

import java.util.Arrays;

import pebl.feature.Capability;
import pebl.feature.ResultFormat;
import pebl.feature.ResultFormatElement;
import pebl.feature.ResultFormatMetric;

public class Capabilities {

    public static final Capability CONFORMANCE = new Capability("Conformance", new ResultFormat(Arrays.asList(
            new ResultFormatElement(ResultFormatMetric.INTEGER, "testCases", "The number of test cases.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.INTEGER, "testCaseSuccesses", "The number of successful test cases.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.INTEGER, "testCaseFailures", "The number of failed test cases.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.BOOLEAN, "testDeployable", "Whether the underlying process model was deployed successfully.", ""),
            new ResultFormatElement(ResultFormatMetric.BOOLEAN, "testSuccessful", "Whether all test cases were successfully.", ""),
            new ResultFormatElement(ResultFormatMetric.TRIVALENT, "testResult", "The overall result based on the number of successful test cases compared to the total number of test cases", "")
    )));

    public static final Capability EXPRESSIVENESS = new Capability("Expressiveness", new ResultFormat(Arrays.asList(
            new ResultFormatElement(ResultFormatMetric.INTEGER, "testCases", "The number of test cases.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.INTEGER, "testCaseSuccesses", "The number of successful test cases.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.INTEGER, "testCaseFailures", "The number of failed test cases.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.BOOLEAN, "testDeployable", "Whether the underlying process model was deployed successfully.", ""),
            new ResultFormatElement(ResultFormatMetric.BOOLEAN, "testSuccessful", "Whether all test cases were successfully.", ""),
            new ResultFormatElement(ResultFormatMetric.TRIVALENT, "testResult", "The overall result based on the number of successful test cases compared to the total number of test cases", "")
    )));

    public static final Capability ROBUSTNESS = new Capability("Robustness", new ResultFormat(Arrays.asList(
            new ResultFormatElement(ResultFormatMetric.INTEGER, "testCases", "The number of test cases.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.INTEGER, "testCaseSuccesses", "The number of successful test cases.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.INTEGER, "testCaseFailures", "The number of failed test cases.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.BOOLEAN, "testDeployable", "Whether the underlying process model was deployed successfully.", ""),
            new ResultFormatElement(ResultFormatMetric.BOOLEAN, "testSuccessful", "Whether all test cases were successfully.", ""),
            new ResultFormatElement(ResultFormatMetric.TRIVALENT, "testResult", "The overall result based on the number of successful test cases compared to the total number of test cases", "")
    )));

    public static final Capability PERFORMANCE = new Capability("Performance", new ResultFormat(Arrays.asList(
            new ResultFormatElement(ResultFormatMetric.AGGREGATED, "process_duration", "The duration of the process instances.", "seconds"),
            new ResultFormatElement(ResultFormatMetric.AGGREGATED, "throughput", "The number of process instances completed per second.", "process instances/second"),
            new ResultFormatElement(ResultFormatMetric.AGGREGATED, "number_of_process_instances", "The number of process instances.", "quantity"),
            new ResultFormatElement(ResultFormatMetric.AGGREGATED, "cpu", "The CPU utilization in percentage.", "%"),
            new ResultFormatElement(ResultFormatMetric.AGGREGATED, "ram", "The RAM utilization in MegaByte.", "MB"),
            new ResultFormatElement(ResultFormatMetric.AGGREGATED, "size_of_stored_data", "The size of data stored in the DBMS in MegaByte.", "MB"),
            new ResultFormatElement(ResultFormatMetric.AGGREGATED, "io", "The IO utilization in MegaByte.", "MB")
    )));

}
