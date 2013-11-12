package betsy.executables.analytics

import ant.tasks.AntUtil
import betsy.executables.analytics.html.HtmlAnalytics
import betsy.executables.analytics.model.CsvReport


class Analyzer {

    final AntBuilder ant = AntUtil.builder()

    String csvFilePath
    String reportsFolderPath

    public void createAnalytics() {
        // load model
        CsvReport csvModel = new CsvReportLoader(csvFile: csvFilePath).load()

        // analytics
        new HtmlAnalytics(report: csvModel).toHtmlReport("${reportsFolderPath}/results.html")
    }

}
