package betsy.bpmn.analytics

import betsy.common.executables.analytics.CsvReportLoader;
import betsy.common.executables.analytics.html.HtmlAnalytics;
import betsy.common.executables.analytics.model.CsvReport;

import java.nio.file.Path;

public class BPMNAnalyzer {

    Path csvFilePath
    Path reportsFolderPath

    public void createAnalytics() {
        // load model
        CsvReport csvModel = new CsvReportLoader(csvFilePath).load()

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
