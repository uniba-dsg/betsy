package betsy.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import betsy.bpmn.reporting.BPMNCsvReport;
import betsy.common.aggregation.TrivalentResult;
import betsy.common.analytics.CsvReportLoader;
import betsy.common.analytics.model.CsvReport;
import betsy.common.analytics.model.Engine;
import betsy.common.analytics.model.Test;
import com.google.common.base.Charsets;
import configuration.bpmn.BPMNProcessRepository;
import pebl.feature.FeatureSet;

public class ResultsPerFeatureSetGenerator {

    private final CsvReport csvReport;

    private final List<pebl.test.Test> processes;

    public ResultsPerFeatureSetGenerator(Path csvPath, String processes) {
        CsvReportLoader loader = new CsvReportLoader(csvPath, new BPMNCsvReport());
        this.csvReport = loader.load();
        if(processes!=null) {
            this.processes = new BPMNProcessRepository().getByName(processes);
        } else {
            this.processes = new ArrayList<>();
            BPMNProcessRepository repo = new BPMNProcessRepository();
            this.processes.addAll(repo.getByNames(csvReport.getNameToTest().keySet().toArray(new String[csvReport.getNameToTest().keySet().size()])));
        }
    }

    public void createAggregatedCSV(Path targetCsvPath) {

        List<AggregatedCsvRow> rows = new ArrayList<>();

        // Find all Feature Sets
        List<FeatureSet> featureSets = processes.stream().map(process -> process.getFeature().getFeatureSet()).distinct().collect(
                Collectors.toList());

        Map<FeatureSet, List<String>> featureSetMap = new TreeMap<>();

        featureSets.forEach(fs -> featureSetMap.put(fs, new ArrayList<>()));

        for(pebl.test.Test p : processes) {
            FeatureSet fs = p.getFeature().getFeatureSet();
            if(featureSetMap.containsKey(fs)) {
                featureSetMap.get(fs).add(p.getName());
            }
        }

        // All test results from CsvReport:
        Collection<Test> allTests =  csvReport.getTests();
        Collection<Engine> allEngines = csvReport.getEngines();

        // For each engine:
        for(Engine engine : allEngines) {
        // For each feature set:
            for(FeatureSet featureSet : featureSetMap.keySet()) {

                AggregatedCsvRow row = new AggregatedCsvRow(featureSet.getName(), engine.getName(), featureSet.getGroup().getName());

                allTests.stream().filter(test -> featureSetMap.get(featureSet).contains(test.getName())).
                        forEach(t -> row.append(t.getEngineToResult().get(engine).getFailed(), t.getEngineToResult().get(engine).getTotal()));

                rows.add(row);
            }
        }

        writeRows(rows, targetCsvPath);
    }

    private void writeRows(final List<AggregatedCsvRow> rows, Path targetCsvPath) {
        List<String> lines = rows.stream().map(AggregatedCsvRow::toString).collect(Collectors.toList());
        try {
            Files.write(targetCsvPath, lines, Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not write file " + targetCsvPath, e);
        }
    }

    /**
     * Generates a CSV file with aggregated results per featureSet (e.g., combining all tests for the language construct "ExclusiveGateway")
     *
     * Resulting format is: FeatureSetName;Engine;Group;TrivalentResult;#OfFailures;#OfTests
     *
     * @param args expected args:   args[0] - Path to standard CSV report
     *                              args[1] - Target path for generated report
     *                              args[2] - process limitation (e.g., only "ACTIVITIES") - all processes in CSV if is not present
     */
    public static void main(String[] args) {
        String processes = null;
        if(args.length==3) {
            processes = args[2];
        }
        ResultsPerFeatureSetGenerator generator = new ResultsPerFeatureSetGenerator(Paths.get(args[0]), processes);
        generator.createAggregatedCSV(Paths.get(args[1]));
    }

    private class AggregatedCsvRow {

        private static final String SEPARATOR = ";";

        private final String featureSet;
        private final String engine;
        private final String group;

        private int failures = 0;
        private int tests = 0;

        public AggregatedCsvRow(String featureSet, String engine, String group) {
            this.featureSet = featureSet;
            this.engine = engine;
            this.group = group;
        }

        public synchronized void append(int failures, int tests) {
            this.failures += failures;
            this.tests += tests;
        }

        private TrivalentResult calculateResult() {
            if(failures==tests) {
                return TrivalentResult.MINUS;
            } else if(failures==0) {
                return TrivalentResult.PLUS;
            } else {
                return TrivalentResult.PLUS_MINUS;
            }
        }


        @Override
        public String toString() {
            return String.join(SEPARATOR, featureSet, engine, group, calculateResult().getSign(), String.valueOf(failures), String.valueOf(tests));
        }
    }

}
