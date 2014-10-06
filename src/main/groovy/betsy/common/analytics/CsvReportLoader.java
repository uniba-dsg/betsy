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
    private final CsvReport csvReport;

    public CsvReportLoader(Path csvFile, CsvReport csvReport) {
        this.csvFile = csvFile;
        this.csvReport = csvReport;

        FileTasks.assertFile(csvFile);
    }

    public CsvReport load() {
        csvReport.setFile(csvFile);

        for (String line : readCsvFile()) {
            String[] fields = line.split(";");
            String testName = fields[0];
            String engineName = fields[1];
            String testGroup = fields[2];
            Integer failedTests = Integer.parseInt(fields[4]);
            Integer totalTests = Integer.parseInt(fields[5]);
            Boolean deployable = fields[6].equals("1");

            Group group = csvReport.getGroup(testGroup);
            Engine engine = csvReport.getEngine(engineName);
            Result result = new Result();

            result.setFailed(failedTests);
            result.setTotal(totalTests);
            result.setDeployable(deployable);
            Test test = csvReport.getTest(testName);
            test.getEngineToResult().put(engine, result);
            group.getTests().add(test);
        }

        return csvReport;
    }

    private List<String> readCsvFile() {
        try {
            return Files.readAllLines(csvFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file " + csvFile, e);
        }
    }

}
