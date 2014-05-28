package betsy.executables.analytics;

import betsy.executables.analytics.html.HtmlAnalytics;
import betsy.executables.analytics.model.CsvReport;
import groovy.util.AntBuilder;

import java.nio.file.Path;

public class Analyzer {
    private Path csvFilePath;
    private Path reportsFolderPath;

    public void createAnalytics() {
        // load model
        CsvReportLoader loader = new CsvReportLoader();

        loader.setCsvFile(csvFilePath);
        CsvReport csvModel = loader.load();

        // analytics
        HtmlAnalytics analytics = new HtmlAnalytics();

        analytics.setReport(csvModel);
        analytics.toHtmlReport(reportsFolderPath.resolve("results.html"));
    }

    public Path getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(Path csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public Path getReportsFolderPath() {
        return reportsFolderPath;
    }

    public void setReportsFolderPath(Path reportsFolderPath) {
        this.reportsFolderPath = reportsFolderPath;
    }
}
