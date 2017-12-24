package pebl.builder;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import betsy.common.model.engine.EngineExtended;
import configuration.MetricTypes;
import pebl.ProcessLanguage;
import pebl.benchmark.test.Test;
import pebl.result.Measurement;
import pebl.result.engine.Engine;
import pebl.result.test.TestResult;
import pebl.xsd.PEBL;

public class PEBLPerformanceResultsAdder {

    public static void addPerformanceResults(PEBL pebl) {
        Test test = pebl.benchmark.tests.stream()
                .filter(t -> t.getCapability().getName().equalsIgnoreCase("performance"))
                .findAny().orElseThrow(IllegalStateException::new);

        // engines
        final Engine engineA = new EngineExtended(ProcessLanguage.BPMN, "engine_a", "N.NN.N", LocalDate.MIN, "Apache-2.0").getEngine();
        pebl.result.engines.add(engineA);
        final Engine engineB = new EngineExtended(ProcessLanguage.BPMN, "engine_b", "N.NN.N", LocalDate.MIN, "Apache-2.0").getEngine();
        pebl.result.engines.add(engineB);

        // tool
        final String benchFlow = "BenchFlow__1";

        // results
        final Engine camunda__7_4_0 = pebl.getEngine("camunda__7_4_0");
        final List<Measurement> measurements = Arrays.asList(
                new Measurement(test.getMetrics().stream().filter(m -> m.getMetricType().equals(MetricTypes.THROUGHPUT)).findAny().orElseThrow(IllegalStateException::new), "1061.27"),
                new Measurement(test.getMetrics().stream().filter(m -> m.getMetricType().equals(MetricTypes.NUMBER_OF_PROCESS_INSTANCES)).findAny().orElseThrow(IllegalStateException::new), "575210"),
                new Measurement(test.getMetrics().stream().filter(m -> m.getMetricType().equals(MetricTypes.PROCESS_DURATION)).findAny().orElseThrow(IllegalStateException::new), "0;434;1.22 +/- 0.02;4.21"),
                new Measurement(test.getMetrics().stream().filter(m -> m.getMetricType().equals(MetricTypes.CPU)).findAny().orElseThrow(IllegalStateException::new), "24.48;56.35;48.52 +/- 0.28;5"),
                new Measurement(test.getMetrics().stream().filter(m -> m.getMetricType().equals(MetricTypes.RAM)).findAny().orElseThrow(IllegalStateException::new), "677.1;1073.15;991.86 +/- 4.24;75.21")
        );
        final TestResult testResultCamunda = new TestResult(test,
                camunda__7_4_0, benchFlow,
                Collections.emptyList(),
                Paths.get(""),
                Collections.emptyList(),
                measurements,
                new HashMap<>(),
                Collections.emptyList());
        addPerformanceEnvironmentData(testResultCamunda);

        pebl.result.testResults.add(testResultCamunda);
    }

    private static void addPerformanceEnvironmentData(TestResult testResultCamunda) {
        testResultCamunda.addExtension("environment.server1.cpu_cores", "64");
        testResultCamunda.addExtension("environment.server1.cpu_power", "1400 MHz");
        testResultCamunda.addExtension("environment.server1.ram", "128");
        testResultCamunda.addExtension("environment.server1.network", "10 Gbit/s");
        testResultCamunda.addExtension("environment.server1.host_operating_system", "Ubuntu 14.04.3 LTS");
        testResultCamunda.addExtension("environment.server1.docker_engine", "1.8.2");
        testResultCamunda.addExtension("environment.server1.configuration", "Ubuntu 14.04.01, Oracle Server 7u79");
        testResultCamunda.addExtension("environment.server1.docker_container", "");
        testResultCamunda.addExtension("environment.server1.purpose", "Load Drivers");

        testResultCamunda.addExtension("environment.server2.cpu_cores", "12");
        testResultCamunda.addExtension("environment.server2.cpu_power", "800 MHz");
        testResultCamunda.addExtension("environment.server2.ram", "64");
        testResultCamunda.addExtension("environment.server2.network", "10 Gbit/s");
        testResultCamunda.addExtension("environment.server2.host_operating_system", "Ubuntu 14.04.3 LTS");
        testResultCamunda.addExtension("environment.server2.docker_engine", "1.8.2");
        testResultCamunda.addExtension("environment.server2.configuration", "Ubuntu 14.04.01, Oracle Server 7u79, Max Java Heap: 32GB, Max DB Connections Number: 100");
        testResultCamunda.addExtension("environment.server2.docker_container", "");
        testResultCamunda.addExtension("environment.server2.purpose", "WfMS");

        testResultCamunda.addExtension("environment.server3.cpu_cores", "64");
        testResultCamunda.addExtension("environment.server3.cpu_power", "2300 MHz");
        testResultCamunda.addExtension("environment.server3.ram", "128");
        testResultCamunda.addExtension("environment.server3.network", "10 Gbit/s");
        testResultCamunda.addExtension("environment.server3.host_operating_system", "Ubuntu 14.04.3 LTS");
        testResultCamunda.addExtension("environment.server3.docker_engine", "1.8.2");
        testResultCamunda.addExtension("environment.server3.configuration", "MySQL Community Server 5.6.26");
        testResultCamunda.addExtension("environment.server3.docker_container", "mysql:5.6.26");
        testResultCamunda.addExtension("environment.server3.purpose", "DBMS");
    }
}
