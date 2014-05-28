package betsy.executables.analytics;

import betsy.executables.analytics.html.HtmlAnalytics;
import betsy.executables.analytics.model.CsvReport;
import betsy.tasks.FileTasks;

import java.nio.file.Path;

public class Analyzer {
    private final Path csvFilePath;
    private final Path reportsFolderPath;

    public Analyzer(Path csvFilePath, Path reportsFolderPath) {
        this.csvFilePath = csvFilePath;
        this.reportsFolderPath = reportsFolderPath;

        FileTasks.assertFile(csvFilePath);
        FileTasks.assertDirectory(reportsFolderPath);
    }

    public void createAnalytics() {
        // load model
        CsvReportLoader loader = new CsvReportLoader(csvFilePath);
        CsvReport csvModel = loader.load();

        // analytics
        HtmlAnalytics analytics = new HtmlAnalytics();

        analytics.setReport(csvModel);
        analytics.toHtmlReport(reportsFolderPath.resolve("results.html"));
    }

}
