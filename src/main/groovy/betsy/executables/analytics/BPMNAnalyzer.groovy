package betsy.executables.analytics;

import ant.tasks.AntUtil;
import betsy.executables.analytics.html.HtmlAnalytics;
import betsy.executables.analytics.model.CsvReport;
import groovy.util.AntBuilder;

import java.nio.file.Path;

public class BPMNAnalyzer {
    final AntBuilder ant = AntUtil.builder()

    Path csvFilePath
    Path reportsFolderPath

    public void createAnalytics() {
        // load model
        CsvReport csvModel = new CsvReportLoader(csvFile: csvFilePath).load()

        // analytics
        new HtmlAnalytics(report: csvModel).toHtmlReport(reportsFolderPath.resolve("results.html"))

        // generate latex
        BPMNLatexSerializer serializer = new BPMNLatexSerializer(
                csvReport: csvModel,
                fileCompact: reportsFolderPath.resolve("results.tex"),
                fileDetailed: reportsFolderPath.resolve("tests.tex")
        )
        serializer.serializeCompactResults()
        serializer.serializeDetailedDescriptions()
    }
}
