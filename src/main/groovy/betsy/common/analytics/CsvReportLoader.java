package betsy.common.analytics;

import betsy.common.analytics.model.*;
import betsy.common.reporting.CsvRow;
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
            CsvRow row = new CsvRow(line);

            Group group = csvReport.getGroup(row.getGroup());
            Engine engine = csvReport.getEngine(row.getEngine());
            Result result = new Result();

            result.setFailed(row.getFailures());
            result.setTotal(row.getTests());
            result.setDeployable(row.isDeployable());
            Test test = csvReport.getTest(row.getName());
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
