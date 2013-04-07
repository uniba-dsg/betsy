package betsy.executables.analytics.html

import org.apache.commons.io.IOUtils;

import betsy.executables.analytics.CsvReportLoader
import betsy.executables.analytics.model.CsvReport
import groovy.text.SimpleTemplateEngine

class HtmlAnalytics {

    CsvReport report

    public static void main(String[] args) {
        new HtmlAnalytics(report: new CsvReportLoader(csvFile: "test/reports/results.csv").load()).toHtmlReport("test/reports/myreport.html")
    }

    void toHtmlReport(String filename) {
        def engine = new SimpleTemplateEngine()
		File file = new File("src" + File.separator + "main" + File.separator + "groovy" + File.separator + "betsy" + File.separator + "executables" + File.separator + "analytics" + File.separator + "html" + File.separator + "HtmlAnalytics.template")
        def template = engine.createTemplate(file.text).make([
                "report": report
        ])

        new File(filename).withWriter("UTF-8") { it.write(template.toString()) }
    }

}
