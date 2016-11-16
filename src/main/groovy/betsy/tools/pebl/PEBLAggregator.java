package betsy.tools.pebl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pebl.HasId;
import pebl.benchmark.feature.Metric;
import pebl.benchmark.feature.MetricType;
import pebl.benchmark.feature.ScriptMetricType;
import pebl.result.Measurement;
import pebl.result.engine.Engine;
import pebl.result.feature.FeatureResult;
import pebl.result.test.TestResult;
import pebl.result.tool.Tool;
import pebl.xsd.PEBL;

public class PEBLAggregator {

    public void computeFeatureResults(PEBL pebl) {
        final Map<String, List<TestResult>> resultsPerEngineTool = pebl.result.testResults.stream().collect(Collectors.groupingBy(t -> t.getEngine().getId() + HasId.SEPARATOR + t.getTool().getId()));

        // everything is recalculated
        pebl.result.featureResults.clear();

        List<Metric> metrics = new LinkedList<>();
        pebl.benchmark.capabilities.forEach(c -> {
            metrics.addAll(c.getMetrics());
            c.getLanguages().forEach(l -> {
                metrics.addAll(l.getMetrics());
                l.getGroups().forEach(g -> {
                    metrics.addAll(g.getMetrics());
                    g.getFeatureSets().forEach(fs -> {
                        metrics.addAll(fs.getMetrics());
                        fs.getFeatures().forEach(f -> {
                            metrics.addAll(f.getMetrics());
                        });
                    });
                });
            });
        });

        for (Metric metric : metrics) {
            final MetricType metricType = metric.getMetricType();
            if (!(metricType instanceof ScriptMetricType)) {
                throw new IllegalStateException("Metric type must be a script metric type");
            }
            ScriptMetricType scriptMetricType = (ScriptMetricType) metricType;

            for (Map.Entry<String, List<TestResult>> entry : resultsPerEngineTool.entrySet()) {

                if (entry.getValue().isEmpty()) {
                    continue;
                }

                // ensure that only the test results relevant for the metric are there
                List<TestResult> testResults = entry.getValue()
                        .stream()
                        .filter(tr -> {
                            final String postfix = HasId.SEPARATOR + metric.getMetricType().getId();
                            final String featureTreeIdMetricReferences = metric.getId().replace(postfix, "");
                            return tr.getTest().getId().startsWith(featureTreeIdMetricReferences);
                        })
                        .collect(Collectors.toList());

                if (testResults.isEmpty()) {
                    continue;
                }

                System.out.println("Computing " + metric.getId() + " for " + entry.getKey());

                String value = computeThroughScript(scriptMetricType, testResults);

                Engine engine = testResults.get(0).getEngine();
                Tool tool = testResults.get(0).getTool();
                Measurement measurement = new Measurement(metric, value);

                final Optional<FeatureResult> featureResultOptional = pebl.result.featureResults.stream()
                        .filter(fr -> fr.getEngine().getId().equals(engine.getId()) && fr.getTool().getId().equals(tool.getId()))
                        .findFirst();
                if(featureResultOptional.isPresent()) {
                    featureResultOptional.get().getMeasurement().add(measurement);
                } else {
                    pebl.result.featureResults.add(new FeatureResult(Arrays.asList(measurement), engine, tool));
                }
            }
        }
    }

    private String computeThroughScript(ScriptMetricType scriptMetricType, List<TestResult> testResults) {
        if (scriptMetricType.getId().equals("testCasesSum")) {
            return String.valueOf(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCases"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .orElse("0")
                    )
                    .mapToLong(Long::parseLong)
                    .sum()
            );
        } else if (scriptMetricType.getId().equals("testCaseSuccessesSum")) {
            return String.valueOf(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCaseSuccesses"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .orElse("0")
                    )
                    .mapToLong(Long::parseLong)
                    .sum()
            );
        } else if (scriptMetricType.getId().equals("testCaseFailuresSum")) {
            return String.valueOf(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCaseFailures"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .orElse("0")
                    )
                    .mapToLong(Long::parseLong)
                    .sum()
            );
        } else if (scriptMetricType.getId().equals("testSuccessfulCount")) {
            return String.valueOf(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testSuccessful"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .orElse("false")
                    )
                    .map(Boolean::parseBoolean)
                    .filter(x -> x)
                    .count()
            );
        } else if (scriptMetricType.getId().equals("testDeployableCount")) {
            return String.valueOf(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testDeployable"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .orElse("false")
                    )
                    .map(Boolean::parseBoolean)
                    .filter(x -> x)
                    .count()
            );
        } else if (scriptMetricType.getId().equals("testResultTrivalentAggregation")) {
            final long testCaseSuccesses = testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCaseSuccesses"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .orElse("0")
                    )
                    .mapToLong(Long::parseLong)
                    .sum();
            final long testCasesFailures = testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCaseFailures"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .orElse("0")
                    )
                    .mapToLong(Long::parseLong)
                    .sum();

            if (testCasesFailures == 0) {
                return "+";
            } else if (testCaseSuccesses == 0) {
                return "-";
            } else {
                return "+/-";
            }
        } else if (scriptMetricType.getId().equals("testsCount")) {
            return String.valueOf(testResults
                    .stream()
                    .count()
            );
        } else if (scriptMetricType.getId().equals("support")) {
            final int max = testResults
                    .stream()
                    .map(tr -> {
                                final String testResult = tr.getMeasurements()
                                        .stream()
                                        .filter(m -> m.getMetric().getMetricType().getId().equals("testResult"))
                                        .findFirst()
                                        .map(Measurement::getValue)
                                        .orElse("-");
                                String extensionLanguageSupport = tr.getTest().getFeature().getExtensions().get(PEBLBuilder.EXTENSION_LANGUAGE_SUPPORT);
                                if (testResult == "+") {
                                    return extensionLanguageSupport;
                                } else {
                                    return "-";
                                }
                            }
                    )
                    .mapToInt(s -> Ternary.from(s).getNumber())
                    .max().orElse(0);

            return Ternary.from(max).getString();
        }

        throw new IllegalStateException("Cannot compute metric " + scriptMetricType);
    }

}
