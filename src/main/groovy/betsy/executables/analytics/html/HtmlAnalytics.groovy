package betsy.executables.analytics.html

import betsy.executables.analytics.CsvReportLoader
import betsy.executables.analytics.model.CsvReport
import groovy.text.SimpleTemplateEngine

class HtmlAnalytics {

    CsvReport report

    public static void main(String[] args) {
        String input = args[0];
        String output = new File(new File(input).parentFile, "myreport.html").getAbsolutePath()

        new HtmlAnalytics(report: new CsvReportLoader(csvFile: input).load()).toHtmlReport(output)
    }

    void toHtmlReport(String filename) {
        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(new File("src\\main\\groovy\\betsy\\executables\\analytics\\html\\HtmlAnalytics.template").text).make([
                "report": report
        ])

        new File(filename).withWriter("UTF-8") { it.write(template.toString()) }
    }

}
