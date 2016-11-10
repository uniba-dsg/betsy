package configuration;

import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.Characteristic;
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

    public static Test addMetrics(Test test) {
        if(test.getCapability().equals(PERFORMANCE)) {
            test.addMetric(ValueType.AGGREGATED, "process_duration", "The duration of the process instances.", "seconds");
            test.addMetric(ValueType.AGGREGATED, "throughput", "The number of process instances completed per second.", "process instances/second");
            test.addMetric(ValueType.AGGREGATED, "number_of_process_instances", "The number of process instances.", "quantity");
            test.addMetric(ValueType.AGGREGATED, "cpu", "The CPU utilization in percentage.", "%");
            test.addMetric(ValueType.AGGREGATED, "ram", "The RAM utilization in MegaByte.", "MB");
            test.addMetric(ValueType.AGGREGATED, "size_of_stored_data", "The size of data stored in the DBMS in MegaByte.", "MB");
            test.addMetric(ValueType.AGGREGATED, "io", "The IO utilization in MegaByte.", "MB");
            test.addMetric(ValueType.AGGREGATED, "executionDuration", "The time the execution of this particular test.", "milliseconds");
            test.addMetric(ValueType.AGGREGATED, "executionTimestamp", "The time the execution of this particular test has been started.", "timestamp");
        } else if(test.getCapability().equals(ROBUSTNESS) || test.getCapability().equals(CONFORMANCE) || test.getCapability().equals(EXPRESSIVENESS)) {
            test.addMetric(ValueType.LONG, "testCases", "The number of test cases.", "quantity");
            test.addMetric(ValueType.LONG, "testCaseSuccesses", "The number of successful test cases.", "quantity");
            test.addMetric(ValueType.LONG, "testCaseFailures", "The number of failed test cases.", "quantity");
            test.addMetric(ValueType.BOOLEAN, "testDeployable", "Whether the underlying process model was deployed successfully.", "");
            test.addMetric(ValueType.BOOLEAN, "testSuccessful", "Whether all test cases were successfully.", "");
            test.addMetric(ValueType.STRING,  "testResult", "The overall result based on the number of successful test cases compared to the total number of test cases", "");
            test.addMetric(ValueType.LONG, "executionDuration", "The time the execution of this particular test.", "milliseconds");
            test.addMetric(ValueType.LONG,  "executionTimestamp", "The time the execution of this particular test has been started.", "timestamp");
        }

        return test;
    }

}
