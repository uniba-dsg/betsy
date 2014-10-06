package betsy.common.analytics;

import betsy.common.analytics.model.*;
import betsy.common.tasks.FileTasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvReportLoader {
    private final Path csvFile;

    public CsvReportLoader(Path csvFile) {
        this.csvFile = csvFile;

        FileTasks.assertFile(csvFile);
    }

    public CsvReport load() {
        CsvReport report = new CsvReport();
        report.setFile(csvFile);

        for (String line : readCsvFile()) {
            String[] fields = line.split(";");
            String testName = fields[0];
            String engineName = fields[1];
            String testGroup = fields[2];
            Integer failedTests = Integer.parseInt(fields[4]);
            Integer totalTests = Integer.parseInt(fields[5]);
            Boolean deployable = fields[6].equals("1");

            Group group = report.getGroup(testGroup);
            Engine engine = report.getEngine(engineName);
            Result result = new Result();

            result.setFailed(failedTests);
            result.setTotal(totalTests);
            result.setDeployable(deployable);
            Test test = report.getTest(testName);
            test.getEngineToResult().put(engine, result);
            group.getTests().add(test);
        }

        return report;
    }

    private List<String> readCsvFile() {
        try {
            return Files.readAllLines(csvFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file " + csvFile, e);
        }
    }

}
