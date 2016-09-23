package betsy.common.analytics;

import java.nio.file.Path;

import betsy.common.analytics.html.HtmlAnalytics;
import betsy.common.analytics.model.CsvReport;
import betsy.common.tasks.FileTasks;

public class Analyzer {
    private final Path csvFilePath;
    private final Path reportsFolderPath;

    public Analyzer(Path csvFilePath, Path reportsFolderPath) {
        this.csvFilePath = csvFilePath;
        this.reportsFolderPath = reportsFolderPath;

        FileTasks.assertFile(csvFilePath);
        FileTasks.assertDirectory(reportsFolderPath);
    }

    public void createAnalytics(CsvReport csvReport) {
        // load model
        CsvReportLoader loader = new CsvReportLoader(csvFilePath, csvReport);
        CsvReport csvModel = loader.load();

        // analytics
        new HtmlAnalytics(csvModel).toHtmlReport(reportsFolderPath.resolve("results.html"));
    }

}
