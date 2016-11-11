package configuration;

import java.util.LinkedList;
import java.util.List;

import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.Characteristic;
import pebl.benchmark.feature.MetricType;
import pebl.benchmark.feature.ValueType;
import pebl.benchmark.test.Test;

public class Capabilities {

    public static final Capability CONFORMANCE = new Capability("Conformance")
            .addCharacteristic(Characteristic.FUNCTIONAL_SUITABILITY)
            .addCharacteristic(Characteristic.FUNCTIONAL_COMPLETENESS)
            .addCharacteristic(Characteristic.FUNCTIONAL_CORRECTNESS);

    public static final Capability EXPRESSIVENESS = new Capability("Expressiveness")
            .addCharacteristic(Characteristic.FUNCTIONAL_SUITABILITY)
            .addCharacteristic(Characteristic.FUNCTIONAL_COMPLETENESS)
            .addCharacteristic(Characteristic.FUNCTIONAL_CORRECTNESS);

    public static final Capability STATIC_ANALYSIS = new Capability("StaticAnalysis")
            .addCharacteristic(Characteristic.USABILITY)
            .addCharacteristic(Characteristic.USER_ERROR_PROTECTION);

    public static final Capability ROBUSTNESS = new Capability("Robustness")
            .addCharacteristic(Characteristic.RELIABILITY)
            .addCharacteristic(Characteristic.FAULT_TOLERANCE);

    public static final Capability PERFORMANCE = new Capability("Performance")
            .addCharacteristic(Characteristic.PERFORMANCE_EFFICIENCY)
            .addCharacteristic(Characteristic.TIME_BEHAVIOUR)
            .addCharacteristic(Characteristic.RESOURCE_UTILISATION)
            .addCharacteristic(Characteristic.CAPACITY);
    private static final MetricType PROCESS_DURATION = new MetricType(ValueType.AGGREGATED, "process_duration", "The duration of the process instances.", "seconds");
    private static final MetricType THROUGHPUT = new MetricType(ValueType.AGGREGATED, "throughput", "The number of process instances completed per second.", "process instances/second");
    private static final MetricType NUMBER_OF_PROCESS_INSTANCES = new MetricType(ValueType.AGGREGATED, "number_of_process_instances", "The number of process instances.", "quantity");
    private static final MetricType CPU = new MetricType(ValueType.AGGREGATED, "cpu", "The CPU utilization in percentage.", "%");
    private static final MetricType RAM = new MetricType(ValueType.AGGREGATED, "ram", "The RAM utilization in MegaByte.", "MB");
    private static final MetricType SIZE_OF_STORED_DATA = new MetricType(ValueType.AGGREGATED, "size_of_stored_data", "The size of data stored in the DBMS in MegaByte.", "MB");
    private static final MetricType IO = new MetricType(ValueType.AGGREGATED, "io", "The IO utilization in MegaByte.", "MB");
    private static final MetricType EXECUTION_DURATION = new MetricType(ValueType.LONG, "executionDuration", "The time the execution of this particular test.", "milliseconds");
    private static final MetricType EXECUTION_TIMESTAMP = new MetricType(ValueType.LONG, "executionTimestamp", "The time the execution of this particular test has been started.", "timestamp");
    private static final MetricType TEST_CASES = new MetricType(ValueType.LONG, "testCases", "The number of test cases.", "quantity");
    private static final MetricType TEST_CASES_SUCCESSFUL = new MetricType(ValueType.LONG, "testCaseSuccesses", "The number of successful test cases.", "quantity");
    private static final MetricType TEST_CASES_FAILURE = new MetricType(ValueType.LONG, "testCaseFailures", "The number of failed test cases.", "quantity");
    private static final MetricType TEST_DEPLOYABLE = new MetricType(ValueType.BOOLEAN, "testDeployable", "Whether the underlying process model was deployed successfully.", "");
    private static final MetricType TEST_SUCCESSFUL = new MetricType(ValueType.BOOLEAN, "testSuccessful", "Whether all test cases were successfully.", "");
    private static final MetricType TEST_RESULT = new MetricType(ValueType.STRING, "testResult", "The overall result based on the number of successful test cases compared to the total number of test cases", "");

    public static Test addMetrics(Test test) {
        if(test.getCapability().equals(PERFORMANCE)) {
            test.addMetric(PROCESS_DURATION);
            test.addMetric(THROUGHPUT);
            test.addMetric(NUMBER_OF_PROCESS_INSTANCES);
            test.addMetric(CPU);
            test.addMetric(RAM);
            test.addMetric(SIZE_OF_STORED_DATA);
            test.addMetric(IO);

            test.addMetric(EXECUTION_DURATION);
            test.addMetric(EXECUTION_TIMESTAMP);

        } else if(test.getCapability().equals(ROBUSTNESS)
                || test.getCapability().equals(CONFORMANCE)
                || test.getCapability().equals(EXPRESSIVENESS)
                || test.getCapability().equals(STATIC_ANALYSIS)) {
            test.addMetric(TEST_CASES);
            test.addMetric(TEST_CASES_SUCCESSFUL);
            test.addMetric(TEST_CASES_FAILURE);
            test.addMetric(TEST_DEPLOYABLE);
            test.addMetric(TEST_SUCCESSFUL);
            test.addMetric(TEST_RESULT);
            test.addMetric(EXECUTION_DURATION);
            test.addMetric(EXECUTION_TIMESTAMP);
        }

        return test;
    }

    public static List<? extends MetricType> getMetricTypes() {
        final LinkedList<MetricType> metricTypes = new LinkedList<>();

        metricTypes.add(PROCESS_DURATION);
        metricTypes.add(THROUGHPUT);
        metricTypes.add(NUMBER_OF_PROCESS_INSTANCES);
        metricTypes.add(CPU);
        metricTypes.add(RAM);
        metricTypes.add(SIZE_OF_STORED_DATA);
        metricTypes.add(IO);
        metricTypes.add(EXECUTION_DURATION);
        metricTypes.add(EXECUTION_TIMESTAMP);

        metricTypes.add(TEST_CASES);
        metricTypes.add(TEST_CASES_SUCCESSFUL);
        metricTypes.add(TEST_CASES_FAILURE);
        metricTypes.add(TEST_DEPLOYABLE);
        metricTypes.add(TEST_SUCCESSFUL);
        metricTypes.add(TEST_RESULT);

        return metricTypes;
    }
}
