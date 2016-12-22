package configuration;

import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.Characteristic;
import pebl.benchmark.test.Test;

public class Capabilities {

    public static final Capability CONFORMANCE = new Capability("Conformance")
            .addCharacteristic(Characteristic.FUNCTIONAL_SUITABILITY)
            .addCharacteristic(Characteristic.FUNCTIONAL_COMPLETENESS)
            .addCharacteristic(Characteristic.FUNCTIONAL_CORRECTNESS)
            .addExtension("feature", "construct configuration")
            .addExtension("featureSet", "construct")
            .addExtension("group", "construct group");

    public static final Capability EXPRESSIVENESS = new Capability("Expressiveness")
            .addCharacteristic(Characteristic.FUNCTIONAL_SUITABILITY)
            .addCharacteristic(Characteristic.FUNCTIONAL_COMPLETENESS)
            .addCharacteristic(Characteristic.FUNCTIONAL_CORRECTNESS)
            .addExtension("feature", "pattern implementation")
            .addExtension("featureSet", "pattern")
            .addExtension("group", "pattern catalog");

    public static final Capability STATIC_ANALYSIS = new Capability("StaticAnalysis")
            .addCharacteristic(Characteristic.USABILITY)
            .addCharacteristic(Characteristic.USER_ERROR_PROTECTION)
            .addExtension("feature", "rule configuration")
            .addExtension("featureSet", "rule")
            .addExtension("group", "rule set");

    public static final Capability ROBUSTNESS = new Capability("Robustness")
            .addCharacteristic(Characteristic.RELIABILITY)
            .addCharacteristic(Characteristic.FAULT_TOLERANCE)
            .addExtension("feature", "mutation")
            .addExtension("featureSet", "message layer")
            .addExtension("group", "fault scenario");

    public static final Capability PERFORMANCE = new Capability("Performance")
            .addCharacteristic(Characteristic.PERFORMANCE_EFFICIENCY)
            .addCharacteristic(Characteristic.TIME_BEHAVIOUR)
            .addCharacteristic(Characteristic.RESOURCE_UTILISATION)
            .addCharacteristic(Characteristic.CAPACITY)
            .addExtension("feature", "experiment")
            .addExtension("featureSet", "")
            .addExtension("group", "");

    public static Test addMetrics(Test test) {
        if(test.getCapability().equals(PERFORMANCE)) {
            test.addMetric(MetricTypes.PROCESS_DURATION);
            test.addMetric(MetricTypes.THROUGHPUT);
            test.addMetric(MetricTypes.NUMBER_OF_PROCESS_INSTANCES);
            test.addMetric(MetricTypes.CPU);
            test.addMetric(MetricTypes.RAM);
            test.addMetric(MetricTypes.SIZE_OF_STORED_DATA);
            test.addMetric(MetricTypes.IO);

            test.addMetric(MetricTypes.EXECUTION_DURATION);
            test.addMetric(MetricTypes.EXECUTION_TIMESTAMP);

        } else if(test.getCapability().equals(ROBUSTNESS)
                || test.getCapability().equals(CONFORMANCE)
                || test.getCapability().equals(EXPRESSIVENESS)
                || test.getCapability().equals(STATIC_ANALYSIS)) {
            test.addMetric(MetricTypes.TEST_CASES);
            test.addMetric(MetricTypes.TEST_CASES_SUCCESSFUL);
            test.addMetric(MetricTypes.TEST_CASES_FAILURE);
            test.addMetric(MetricTypes.TEST_DEPLOYABLE);
            test.addMetric(MetricTypes.TEST_SUCCESSFUL);
            test.addMetric(MetricTypes.TEST_RESULT);
            test.addMetric(MetricTypes.EXECUTION_DURATION);
            test.addMetric(MetricTypes.EXECUTION_TIMESTAMP);
        }

        return test;
    }

}
