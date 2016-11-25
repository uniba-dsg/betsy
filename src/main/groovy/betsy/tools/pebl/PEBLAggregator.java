package betsy.tools.pebl;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import pebl.HasId;
import pebl.benchmark.feature.Metric;
import pebl.benchmark.feature.MetricType;
import pebl.result.Measurement;
import pebl.result.engine.Engine;
import pebl.result.feature.FeatureResult;
import pebl.result.test.TestResult;
import pebl.xsd.PEBL;

public class PEBLAggregator {

    public void computeFeatureResults(PEBL pebl) {
        final Map<String, List<TestResult>> resultsPerEngineTool = pebl.result.testResults.stream().collect(Collectors.groupingBy(HasId::getId));

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
            if (metricType.getGroovyScript() == null || metricType.getGroovyScript().trim().isEmpty()) {
                throw new IllegalStateException("Metric type must be a script metric type");
            }
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

                String value = computeThroughScript(metric, testResults);

                Engine engine = testResults.get(0).getEngine();
                Measurement measurement = new Measurement(metric, value);

                final Optional<FeatureResult> featureResultOptional = pebl.result.featureResults.stream()
                        .filter(fr -> fr.getEngine().getId().equals(engine.getId()))
                        .findFirst();
                if (featureResultOptional.isPresent()) {
                    featureResultOptional.get().getMeasurement().add(measurement);
                } else {
                    pebl.result.featureResults.add(new FeatureResult(Arrays.asList(measurement), engine));
                }
            }
        }
    }

    private String computeThroughScript(Metric metric, List<TestResult> testResults) {
        MetricType metricType = metric.getMetricType();
        if (metricType.getId().equals("testCasesSum")) {
            return String.valueOf(getTestCases(testResults));
        } else if (metricType.getId().equals("testCaseSuccessesSum")) {
            return String.valueOf(getTestCaseSuccesses(testResults));
        } else if (metricType.getId().equals("testCaseFailuresSum")) {
            return String.valueOf(getTestCaseFailures(testResults));
        } else if (metricType.getId().equals("testSuccessfulCount")) {
            return String.valueOf(getTestSuccessfulCount(testResults));
        } else if (metricType.getId().equals("testDeployableCount")) {
            return String.valueOf(getTestDeployable(testResults));
        } else if (metricType.getId().equals("testResultTrivalentAggregation")) {
            return testResults.stream().map(this::getResultTrivalentAggregation).reduce(Ternary.PLUS, Ternary::aggregate).getString();
        } else if (metricType.getId().equals("testsCount")) {
            return String.valueOf((long) testResults.size());
        } else if (metricType.getId().equals("patternSupport")) {
            return getSupport(testResults).getString();
        }   else if (metricType.getId().equals("patternImplementationSupport")) {
            return getSupport(testResults.get(0)).getString();
        } else if (metricType.getId().equals("patternImplementationFulfilledLanguageSupport")) {
            return String.valueOf(!getSupport(testResults.get(0)).equals(Ternary.MINUS));
        } else if (metricType.getId().equals("patternFulfilledLanguageSupport")) {
            Ternary support = getSupport(testResults);
            Ternary languageSupportOfFeatureSet = Ternary.from(testResults.get(0).getTest().getFeature().getFeatureSet().getExtensions().get("languageSupport"));

            return String.valueOf(support.equals(languageSupportOfFeatureSet));
        }

        throw new IllegalStateException("Cannot compute metric " + metricType);
    }

    private long getTestSuccessfulCount(List<TestResult> testResults) {
        return testResults
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
                .count();
    }

    private Ternary getResultTrivalentAggregation(TestResult testResult) {
        final long testCaseSuccesses = getTestCaseSuccesses(Collections.singletonList(testResult));
        final long testCasesFailures = getTestCaseFailures(Collections.singletonList(testResult));

        if (testCasesFailures == 0) {
            return Ternary.PLUS;
        } else if (testCaseSuccesses == 0) {
            return Ternary.MINUS;
        } else {
            return Ternary.PLUS_MINUS;
        }
    }

    private Ternary getSupport(TestResult testResult) {
        return Optional.of(testResult)
                .map(tr -> {
                            final String result = tr.getMeasurements()
                                    .stream()
                                    .filter(m -> m.getMetric().getMetricType().getId().equals("testResult"))
                                    .findFirst()
                                    .map(Measurement::getValue)
                                    .orElse("-");
                            String extensionLanguageSupport = tr.getTest().getFeature().getExtensions().get(PEBLBuilder.EXTENSION_LANGUAGE_SUPPORT);
                            if (result.equals("+")) {
                                return Ternary.from(extensionLanguageSupport);
                            } else {
                                return Ternary.MINUS;
                            }
                        }
                ).orElse(Ternary.MINUS);
    }

    private Ternary getSupport(List<TestResult> testResults) {
        Ternary languageSupportOfFeatureSet = Ternary.from(testResults.get(0).getTest().getFeature().getFeatureSet().getExtensions().get("languageSupport"));
        return testResults.stream().map(this::getSupport).map(t -> t.atMost(languageSupportOfFeatureSet)).reduce(Ternary.MINUS, Ternary::max);
    }

    private long getTestDeployable(List<TestResult> testResults) {
        return testResults
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
                .count();
    }

    private long getTestCaseSuccesses(List<TestResult> testResults) {
        return testResults
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
    }

    private long getTestCases(List<TestResult> testResults) {
        return testResults
                .stream()
                .map(tr -> tr.getMeasurements()
                        .stream()
                        .filter(m -> m.getMetric().getMetricType().getId().equals("testCases"))
                        .findFirst()
                        .map(Measurement::getValue)
                        .orElse("0")
                )
                .mapToLong(Long::parseLong)
                .sum();
    }

    private long getTestCaseFailures(List<TestResult> testResults) {
        return testResults
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
    }

}
